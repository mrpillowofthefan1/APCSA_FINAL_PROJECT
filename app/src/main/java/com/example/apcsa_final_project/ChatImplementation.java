package com.example.apcsa_final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.material.navigation.NavigationView;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
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
import com.google.genai.Chat;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jspecify.annotations.NonNull;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class GenerateTextFromTextInput {
    private Client client;
    private Chat chat;
    public GenerateTextFromTextInput() {
        client = Client.builder()
                .apiKey(BuildConfig.GEMINI_API_KEY)
                .build();
        this.chat = client.chats.create("gemini-3.1-flash-lite-preview");
    }
    public String userTextToResponse(String userMsg){
        GenerateContentResponse response =
                chat.sendMessage(userMsg);
        return response.text();
    }
}

public class ChatImplementation extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private EditText editMessage;
    private Button buttonSend;
    private GenerateTextFromTextInput aiClient;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final String PREFS_NAME = "ChatPrefs";
    private static final String KEY_CHAT_HISTORY = "chat_history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.recycler_gchat);
        editMessage = findViewById(R.id.edit_gchat_message);
        buttonSend = findViewById(R.id.button_gchat_send);

        toolbar.setTitle("Chat");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        loadChatHistory();
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);
        if (!chatMessages.isEmpty()) {
            recyclerView.scrollToPosition(chatMessages.size() - 1);
        }

        aiClient = new GenerateTextFromTextInput();

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessage(message);
                    editMessage.setText("");
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    Intent intent = new Intent(ChatImplementation.this, Home.class);
                    intent.putExtra("ROLE", getIntent().getStringExtra("ROLE"));
                    intent.putExtra("DISPLAY_NAME", getIntent().getStringExtra("DISPLAY_NAME"));
                    startActivity(intent);
                    toolbar.setTitle("Home");
                }
                if (item.getItemId() == R.id.nav_forum) {
                    Intent intent = new Intent(ChatImplementation.this, Forum.class);
                    intent.putExtra("ROLE", getIntent().getStringExtra("ROLE"));
                    intent.putExtra("DISPLAY_NAME", getIntent().getStringExtra("DISPLAY_NAME"));
                    startActivity(intent);
                    toolbar.setTitle("Forum");
                }
                if (item.getItemId() == R.id.nav_start_guide) {
                    Intent intent = new Intent(ChatImplementation.this, StartGuide.class);
                    intent.putExtra("ROLE", getIntent().getStringExtra("ROLE"));
                    intent.putExtra("DISPLAY_NAME", getIntent().getStringExtra("DISPLAY_NAME"));
                    startActivity(intent);
                    toolbar.setTitle("Start Guide");
                }
                if (item.getItemId() == R.id.nav_market) {
                    Intent intent = new Intent(ChatImplementation.this, Market.class);
                    intent.putExtra("ROLE", getIntent().getStringExtra("ROLE"));
                    intent.putExtra("DISPLAY_NAME", getIntent().getStringExtra("DISPLAY_NAME"));
                    startActivity(intent);
                    toolbar.setTitle("Market");
                }
                if (item.getItemId() == R.id.nav_chat) {
                    toolbar.setTitle("Chat");
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        });
    }

    // sends user msg to ai and gets a response back
    private void sendMessage(String message) {
        chatMessages.add(new ChatMessage(message, true));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerView.scrollToPosition(chatMessages.size() - 1);
        saveChatHistory();

        executorService.execute(() -> {
            try {
                String response = aiClient.userTextToResponse(message);
                runOnUiThread(() -> {
                    chatMessages.add(new ChatMessage(response, false));
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    recyclerView.scrollToPosition(chatMessages.size() - 1);
                    saveChatHistory();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    chatMessages.add(new ChatMessage("Error: " + e.getMessage(), false));
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    recyclerView.scrollToPosition(chatMessages.size() - 1);
                    saveChatHistory();
                });
            }
        });
    }

    // saves chats so they don't disappear
    private void saveChatHistory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(chatMessages);
        editor.putString(KEY_CHAT_HISTORY, json);
        editor.apply();
    }

    private void loadChatHistory() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(KEY_CHAT_HISTORY, null);
        Type type = new TypeToken<ArrayList<ChatMessage>>() {}.getType();
        chatMessages = gson.fromJson(json, type);
        if (chatMessages == null) {
            chatMessages = new ArrayList<>();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
