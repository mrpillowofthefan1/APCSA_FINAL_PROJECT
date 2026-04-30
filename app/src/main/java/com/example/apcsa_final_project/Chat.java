package com.example.apcsa_final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

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
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import org.jspecify.annotations.NonNull;

public class Chat extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);

        toolbar.setTitle("Chat");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle the selected item based on its ID
                if (item.getItemId() == R.id.nav_home) {
                    Intent intent = new Intent(Chat.this, Home.class);
                    startActivity(intent);
                    toolbar.setTitle("Home");
                }

                if (item.getItemId() == R.id.nav_forum) {
                    Intent intent = new Intent(Chat.this, Forum.class);
                    startActivity(intent);
                    toolbar.setTitle("Forum");
                }

                if (item.getItemId() == R.id.nav_start_guide) {
                    Intent intent = new Intent(Chat.this, StartGuide.class);
                    startActivity(intent);
                    toolbar.setTitle("Start Guide");
                }

                if (item.getItemId() == R.id.nav_market) {
                    Intent intent = new Intent(Chat.this, Market.class);
                    startActivity(intent);
                    toolbar.setTitle("Market");
                }

                if (item.getItemId() == R.id.nav_chat) {
                    // Current activity
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
}