package com.example.apcsa_final_project.data;

import com.example.apcsa_final_project.data.model.*;
import com.example.apcsa_final_project.NetworkConfig;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private static final String BASE_URL = NetworkConfig.BASE_URL;
    private final OkHttpClient client = new OkHttpClient();

    public Result<LoggedInUser> login(String username, String password, String role) {
        return performAuthAction("login.php", username, password, role);
    }

    public Result<LoggedInUser> register(String username, String password, String role) {
        return performAuthAction("register.php", username, password, role);
    }

    private Result<LoggedInUser> performAuthAction(String endpoint, String username, String password, String role) {
        try {
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("password", password);
            json.put("role", role);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + endpoint)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String responseData = response.body().string();

            if (response.isSuccessful()) {
                JSONObject resJson = new JSONObject(responseData);
                String userId = resJson.getString("userId");
                String displayName = resJson.getString("username");
                String userRole = resJson.getString("role");

                LoggedInUser user;
                if ("Farmer".equalsIgnoreCase(userRole)) {
                    user = new FarmerUser(userId, displayName);
                } else {
                    user = new NormalUser(userId, displayName);
                }
                return new Result.Success<>(user);
            } else {
                return new Result.Error(new IOException("Error: " + responseData));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Network error", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
