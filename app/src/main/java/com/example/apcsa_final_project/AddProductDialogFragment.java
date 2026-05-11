package com.example.apcsa_final_project;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddProductDialogFragment extends DialogFragment {

    private EditText editName, editPrice, editDescription, editSpecificDetail;
    private Spinner spinnerType;
    private Button buttonSubmit, buttonSelectImage;
    private ImageView imagePreview;
    private ProgressBar progressBar;
    private String farmerUsername;
    private String encodedImage = "";

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imagePreview.setImageBitmap(bitmap);
                        encodedImage = encodeImage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    public static AddProductDialogFragment newInstance(String username) {
        AddProductDialogFragment fragment = new AddProductDialogFragment();
        Bundle args = new Bundle();
        args.putString("USERNAME", username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            farmerUsername = getArguments().getString("USERNAME");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_add_product, container, false);
        editName = view.findViewById(R.id.edit_item_name);
        editPrice = view.findViewById(R.id.edit_price);
        editDescription = view.findViewById(R.id.edit_description);
        editSpecificDetail = view.findViewById(R.id.edit_specific_detail);
        spinnerType = view.findViewById(R.id.spinner_type);
        buttonSubmit = view.findViewById(R.id.button_submit);
        buttonSelectImage = view.findViewById(R.id.button_select_image);
        imagePreview = view.findViewById(R.id.image_preview);
        progressBar = view.findViewById(R.id.loading_progress);
        String[] types = {"Plant", "Tool", "Seed", "Produce"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
        buttonSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });
        buttonSubmit.setOnClickListener(v -> submitProduct());
        return view;
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 480;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void submitProduct() {
        String name = editName.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();
        String specificDetail = editSpecificDetail.getText().toString().trim();
        if (name.isEmpty() || price.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all basic fields", Toast.LENGTH_SHORT).show();
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
            json.put("image", encodedImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(NetworkConfig.BASE_URL + "add_product.php").post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    buttonSubmit.setEnabled(true);
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    buttonSubmit.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Product listed successfully!", Toast.LENGTH_LONG).show();
                        if (getActivity() instanceof Market) {
                            ((Market) getActivity()).fetchProducts();
                        }
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), "Server Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
