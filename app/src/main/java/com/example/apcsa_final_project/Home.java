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

import org.jspecify.annotations.NonNull;

public class Home extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            return insets;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);

        String role = getIntent().getStringExtra("ROLE");
        if ("Farmer".equalsIgnoreCase(role)) {
            toolbar.setTitle("Farmer Home");
        } else {
            toolbar.setTitle("Customer Home");
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.nav_home) {
                } else if (item.getItemId() == R.id.nav_forum) {
                    intent = new Intent(Home.this, Forum.class);
                } else if (item.getItemId() == R.id.nav_start_guide) {
                    intent = new Intent(Home.this, StartGuide.class);
                } else if (item.getItemId() == R.id.nav_market) {
                    intent = new Intent(Home.this, Market.class);
                } else if (item.getItemId() == R.id.nav_chat) {
                    intent = new Intent(Home.this, ChatImplementation.class);
                }

                if (intent != null) {
                    intent.putExtra("ROLE", getIntent().getStringExtra("ROLE"));
                    intent.putExtra("DISPLAY_NAME", getIntent().getStringExtra("DISPLAY_NAME"));
                    startActivity(intent);
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
