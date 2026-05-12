package com.example.apcsa_final_project;

import java.nio.file.Paths;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFiles;
import static spark.Spark.port;
import android.os.Build;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.stripe.StripeClient;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

// mock server for payment (handled by php now)
public class PaymentServer {
    private static Gson gson = new Gson();
    private static final String Stripe_api_key = BuildConfig.STRIPE_API_KEY;

    static class CreatePaymentItem {
        @SerializedName("id")
        String id;
        public String getId() { return id; }
        @SerializedName("amount")
        Long amount;
        public Long getAmount() { return amount; }
    }

    static class CreatePayment {
        @SerializedName("items")
        CreatePaymentItem[] items;
        public CreatePaymentItem[] getItems() { return items; }
    }

    static class CreatePaymentResponse {
        private String clientSecret;
        private String paymentIntentId;
        public CreatePaymentResponse(String clientSecret, String paymentIntentId) {
            this.clientSecret = clientSecret;
            this.paymentIntentId = paymentIntentId;
        }
    }

    // calc order amount from items
    static int calculateOrderAmount(CreatePaymentItem[] items) {
        int total = 0;
        for (CreatePaymentItem item : items) {
            total += item.getAmount();
        }
        return total;
    }

    public static StripeClient client = new StripeClient(Stripe_api_key);

    public static void main(String[] args) {
        port(4242);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            staticFiles.externalLocation(Paths.get("public").toAbsolutePath().toString());
        }

        // endpoint for stripe payments
        post("/create-payment-intent", (request, response) -> {
            response.type("application/json");
            CreatePayment postBody = gson.fromJson(request.body(), CreatePayment.class);

            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                            .setAmount(new Long(calculateOrderAmount(postBody.getItems())))
                            .setCurrency("usd")
                            .setAutomaticPaymentMethods(
                                    PaymentIntentCreateParams.AutomaticPaymentMethods
                                            .builder()
                                            .setEnabled(true)
                                            .build()
                            )
                            .build();

            PaymentIntent paymentIntent = client.v1().paymentIntents().create(params);

            CreatePaymentResponse paymentResponse = new CreatePaymentResponse(paymentIntent.getClientSecret(), paymentIntent.getId());
            return gson.toJson(paymentResponse);
        });
    }
}
