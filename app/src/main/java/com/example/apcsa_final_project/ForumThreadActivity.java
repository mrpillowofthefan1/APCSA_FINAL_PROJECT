package com.example.apcsa_final_project;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

// activity for viewing one forum thread and its comments
public class ForumThreadActivity extends AppCompatActivity {
    private int threadId;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private final List<CommentObject> commentList = new ArrayList<>();
    private EditText editCommentContent;
    private final OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = NetworkConfig.BASE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_thread);

        // get intent data from forum list
        threadId = getIntent().getIntExtra("THREAD_ID", 0);
        String threadTitle = getIntent().getStringExtra("THREAD_TITLE");

        Toolbar toolbar = findViewById(R.id.thread_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(threadTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.comments_recycler_view);
        editCommentContent = findViewById(R.id.edit_comment_content);
        ImageButton buttonSendComment = findViewById(R.id.button_send_comment);

        // setup list for comments
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentAdapter(commentList);
        recyclerView.setAdapter(adapter);

        fetchComments();

        buttonSendComment.setOnClickListener(v -> submitComment());
    }

    // gets comments for this thread from php
    private void fetchComments() {
        Request request = new Request.Builder()
                .url(BASE_URL + "get_comments.php?thread_id=" + threadId)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(ForumThreadActivity.this, "Network Error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray array = new JSONArray(response.body().string());
                        commentList.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            commentList.add(new CommentObject(
                                    obj.getString("username"),
                                    obj.getString("role"),
                                    obj.getString("content"),
                                    obj.getString("created_at")
                            ));
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // sends a reply to the server
    private void submitComment() {
        String content = editCommentContent.getText().toString().trim();
        if (content.isEmpty()) return;

        // find who is logged in
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

        // make json for post request
        JSONObject json = new JSONObject();
        try {
            json.put("thread_id", threadId);
            json.put("username", username);
            json.put("role", role);
            json.put("content", content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(BASE_URL + "add_comment.php").post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(ForumThreadActivity.this, "Failed to reply", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        editCommentContent.setText("");
                        fetchComments();
                    });
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }
}
