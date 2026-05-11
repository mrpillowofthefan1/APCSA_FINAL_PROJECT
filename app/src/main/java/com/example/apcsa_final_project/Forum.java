package com.example.apcsa_final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import androidx.annotation.NonNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.apcsa_final_project.data.LoginRepository;
import com.example.apcsa_final_project.data.LoginDataSource;
import com.example.apcsa_final_project.data.model.LoggedInUser;

public class Forum extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ForumAdapter adapter;
    private final List<PostObject> postList = new ArrayList<>();
    private EditText editPostContent;
    private final OkHttpClient client = new OkHttpClient();

    private static final String BASE_URL = NetworkConfig.BASE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forum2);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forum_main_content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 
                        ime.bottom > 0 ? ime.bottom : systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.forum_recycler_view);
        editPostContent = findViewById(R.id.edit_post_content);
        ImageButton buttonSendPost = findViewById(R.id.button_send_post);

        toolbar.setTitle("Community Forum");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ForumAdapter(postList);
        recyclerView.setAdapter(adapter);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        fetchThreads();

        buttonSendPost.setOnClickListener(v -> submitThread());

        navigationView.setNavigationItemSelectedListener(item -> {
            Intent intent = null;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                intent = new Intent(Forum.this, Home.class);
            } else if (id == R.id.nav_forum) {
            } else if (id == R.id.nav_start_guide) {
                intent = new Intent(Forum.this, StartGuide.class);
            } else if (id == R.id.nav_market) {
                intent = new Intent(Forum.this, Market.class);
            } else if (id == R.id.nav_chat) {
                intent = new Intent(Forum.this, ChatImplementation.class);
            }

            if (intent != null) {
                intent.putExtras(getIntent());
                startActivity(intent);
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    // gets threads from the server
    private void fetchThreads() {
        Request request = new Request.Builder().url(BASE_URL + "get_threads.php").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(Forum.this, "Network Error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    try {
                        JSONArray array = new JSONArray(responseBody);
                        postList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            postList.add(new PostObject(
                                    obj.getInt("id"),
                                    obj.getString("username"),
                                    obj.getString("role"),
                                    obj.getString("title"),
                                    obj.getString("created_at")
                            ));
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(Forum.this, "Data Error", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    // sends a new thread to the db
    private void submitThread() {
        String title = editPostContent.getText().toString().trim();
        if (title.isEmpty()) return;

        LoggedInUser user = LoginRepository.getInstance(new LoginDataSource()).getUser();
        
        String username = "Anonymous";
        String role = "User";
        
        if (user != null) {
            username = user.getDisplayName();
            role = user.getRole();
        } else {
            String extraName = getIntent().getStringExtra("DISPLAY_NAME");
            String extraRole = getIntent().getStringExtra("ROLE");
            if (extraName != null) username = extraName;
            if (extraRole != null) role = extraRole;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("role", role);
            json.put("title", title);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(BASE_URL + "add_thread.php").post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(Forum.this, "Failed to post", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        editPostContent.setText("");
                        fetchThreads();
                    });
                }
            }
        });
    }
}
