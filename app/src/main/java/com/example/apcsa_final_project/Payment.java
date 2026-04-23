package com.example.apcsa_final_project;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.stripe.StripeClient;
import com.stripe.model.CustomerSession;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerSessionCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.google.gson.Gson;
import com.stripe.exception.StripeException;

import java.util.HashMap;
import java.util.Map;

public class Payment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    // Don't put any keys in code. See https://docs.stripe.com/keys-best-practices.
    // Find your keys at https://dashboard.stripe.com/apikeys.
    StripeClient stripeClient = new StripeClient(System.getenv("STRIPE_API_KEY"));
    Gson gson = new Gson();

    /**
     * This method contains the logic that was originally in a Spark 'post' block.
     * Note: In a real app, this logic should reside on a secure backend server,
     * not within the Android application, to avoid exposing your secret API key.
     */
    public String createPaymentSheet() throws StripeException {
        // Use an existing Account ID if this is a returning customer.
        com.stripe.param.v2.core.AccountCreateParams accountParams =
                com.stripe.param.v2.core.AccountCreateParams.builder()
                        .setConfiguration(com.stripe.param.v2.core.AccountCreateParams.Configuration.builder()
                                .setCustomer(com.stripe.param.v2.core.AccountCreateParams.Configuration.Customer.builder().build())
                                .build())
                        .build();
        
        com.stripe.model.v2.core.Account customerAccount =
                stripeClient.v2().core().accounts().create(accountParams);

        CustomerSessionCreateParams params =
                CustomerSessionCreateParams.builder()
                        .setCustomerAccount(customerAccount.getId())
                        .setComponents(CustomerSessionCreateParams.Components.builder().build())
                        .putExtraParam("components[mobile_payment_element][enabled]", true)
                        .putExtraParam(
                                "components[mobile_payment_element][features][payment_method_save]",
                                "enabled"
                        )
                        .putExtraParam(
                                "components[mobile_payment_element][features][payment_method_redisplay]",
                                "enabled"
                        )
                        .putExtraParam(
                                "components[mobile_payment_element][features][payment_method_remove]",
                                "enabled"
                        )
                        .build();
        CustomerSession customerSession = stripeClient.v1().customerSessions().create(params);
        PaymentIntentCreateParams paymentIntentParams =
                PaymentIntentCreateParams.builder()
                        .setAmount(1099L)
                        .setCurrency("eur")
                        .setCustomerAccount(customerAccount.getId())

                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        PaymentIntent paymentIntent = stripeClient.v1().paymentIntents().create(paymentIntentParams);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("paymentIntent", paymentIntent.getClientSecret());
        responseData.put("customerSessionClientSecret", customerSession.getClientSecret());

        responseData.put("customer_account", customerAccount.getId());
        responseData.put("publishableKey", "pk_test_51TM9wU6BqaThljT2PG5Q7gU4nXsxdpq7o3a20aCqBWbTS97aMDGvtHDs0Sby24CfqEuYIV3Yvn2O1R7yJA7DfmHY00xvPxcY1b");

        return gson.toJson(responseData);
    }
}
