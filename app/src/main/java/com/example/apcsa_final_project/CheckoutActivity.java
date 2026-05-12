package com.example.apcsa_final_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

// handles stripe checkout process
public class CheckoutActivity extends AppCompatActivity {
    private static final String TAG = "CheckoutActivity";
    private static final String BACKEND_URL = NetworkConfig.BASE_URL + "checkout.php";

    private String checkoutUrl;
    private Button payButton;
    private TextView itemNameText;
    private TextView itemPriceText;
    private android.webkit.WebView checkoutWebView;
    private View detailsContainer;
    private View progressBar;

    private String itemName;
    private long itemPriceCents;
    private String stripePriceId;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // get product info from market
        itemName = getIntent().getStringExtra("ITEM_NAME");
        itemPriceCents = getIntent().getLongExtra("ITEM_PRICE_CENTS", 0);
        stripePriceId = getIntent().getStringExtra("STRIPE_PRICE_ID");

        itemNameText = findViewById(R.id.item_name_text);
        itemPriceText = findViewById(R.id.item_price_text);
        payButton = findViewById(R.id.pay_button);
        checkoutWebView = findViewById(R.id.checkout_webview);
        detailsContainer = findViewById(R.id.details_container);
        progressBar = findViewById(R.id.checkout_progress);

        // setup webview for stripe
        android.webkit.WebSettings webSettings = checkoutWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        checkoutWebView.setWebViewClient(new android.webkit.WebViewClient());

        if (itemName != null) {
            itemNameText.setText(itemName);
            itemPriceText.setText(String.format(java.util.Locale.US, "$%.2f", itemPriceCents / 100.0));
        }

        payButton.setText("Open Stripe Checkout");
        payButton.setOnClickListener(this::onPayClicked);
        payButton.setEnabled(false);

        fetchCheckoutSession();
    }

    // gets session url from php
    private void fetchCheckoutSession() {
        JSONObject jsonPayload = new JSONObject();
        try {
            if (stripePriceId != null && !stripePriceId.isEmpty()) {
                jsonPayload.put("priceId", stripePriceId);
            }
            jsonPayload.put("amount", itemPriceCents);
            jsonPayload.put("name", itemName);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create JSON payload", e);
        }

        RequestBody requestBody = RequestBody.create(
                jsonPayload.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(BACKEND_URL)
                .post(requestBody)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Network error connecting to backend: " + e.getMessage());
                showAlert("Connection Error", 
                    "Could not reach backend at " + BACKEND_URL + ".\n\n" +
                    "1. Is your Stripe server running?\n" +
                    "2. If using a real phone, use your PC's IP instead of 10.0.2.2.\n" +
                    "3. Check if your firewall is blocking port 4242.");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseBody = response.body() != null ? response.body().string() : "";
                
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Server error: " + response.code() + " - " + responseBody);
                    showAlert("Server Error (" + response.code() + ")", "Backend returned:\n" + responseBody);
                } else {
                    try {
                        JSONObject responseJson = new JSONObject(responseBody);
                        checkoutUrl = responseJson.getString("url");
                        
                        // auto load webview when we get url
                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            checkoutWebView.setVisibility(View.VISIBLE);
                            checkoutWebView.loadUrl(checkoutUrl);
                        });
                        
                        Log.i(TAG, "Checkout Session URL received and loading: " + checkoutUrl);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse session response: " + responseBody, e);
                        showAlert("Data Format Error", "Server sent invalid data:\n" + responseBody);
                    }
                }
            }
        });
    }

    // click handler for payment
    private void onPayClicked(View view) {
        if (checkoutUrl != null) {
            detailsContainer.setVisibility(View.GONE);
            checkoutWebView.setVisibility(View.VISIBLE);
            checkoutWebView.loadUrl(checkoutUrl);
        }
    }

    // handles android back button
    @Override
    public void onBackPressed() {
        if (checkoutWebView.getVisibility() == View.VISIBLE) {
            if (checkoutWebView.canGoBack()) {
                checkoutWebView.goBack();
            } else {
                checkoutWebView.setVisibility(View.GONE);
                detailsContainer.setVisibility(View.VISIBLE);
            }
        } else {
            super.onBackPressed();
        }
    }

    // helper for alerts
    private void showAlert(String title, String message) {
        runOnUiThread(() -> {
            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Ok", null)
                    .show();
        });
    }
}
