package com.example.apcsa_final_project;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddProductActivity extends AppCompatActivity {
    private static final String TAG = "AddProductActivity";
    private static final String BACKEND_URL = "http://192.168.0.88:8888/src/add_product.php";

    private EditText editName, editPrice, editDescription, editSpecificDetail;
    private Spinner spinnerType;
    private Button buttonSubmit;
    private ProgressBar progressBar;
    private String farmerUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        farmerUsername = getIntent().getStringExtra("DISPLAY_NAME");
        if (farmerUsername == null) farmerUsername = "Farmer";

        editName = findViewById(R.id.edit_item_name);
        editPrice = findViewById(R.id.edit_price);
        editDescription = findViewById(R.id.edit_description);
        editSpecificDetail = findViewById(R.id.edit_specific_detail);
        spinnerType = findViewById(R.id.spinner_type);
        buttonSubmit = findViewById(R.id.button_submit);
        progressBar = findViewById(R.id.loading_progress);

        String[] types = {"Plant", "Tool", "Seed", "Produce"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        buttonSubmit.setOnClickListener(v -> submitProduct());
    }

    private void submitProduct() {
        String name = editName.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();
        String specificDetail = editSpecificDetail.getText().toString().trim();

        if (name.isEmpty() || price.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all basic fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonSubmit.setEnabled(false);

        JSONObject json = new JSONObject();
        try {
            json.put("item_name", name);
            json.put("price", price);
            json.put("description", description);
            json.put("username", farmerUsername);
            json.put("type", type);
            json.put("specific_detail", specificDetail);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(BACKEND_URL).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    buttonSubmit.setEnabled(true);
                    showAlert("Error", "Failed to connect to server: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    buttonSubmit.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(AddProductActivity.this, "Product listed successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        showAlert("Error", "Server returned error: " + responseBody);
                    }
                });
            }
        });
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}