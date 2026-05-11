package com.example.apcsa_final_project;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.apcsa_final_project.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import org.jspecify.annotations.NonNull;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(toggle);

        toggle.syncState();
        toolbar.setTitle("Home");

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                if (item.getItemId() == R.id.nav_home) {
                    intent = new Intent(MainActivity.this, Home.class);
                    toolbar.setTitle("Home");
                } else if (item.getItemId() == R.id.nav_forum) {
                    intent = new Intent(MainActivity.this, Forum.class);
                    toolbar.setTitle("Forum");
                } else if (item.getItemId() == R.id.nav_start_guide) {
                    intent = new Intent(MainActivity.this, StartGuide.class);
                    toolbar.setTitle("Start Guide");
                } else if (item.getItemId() == R.id.nav_market) {
                    intent = new Intent(MainActivity.this, Market.class);
                    toolbar.setTitle("Market");
                } else if (item.getItemId() == R.id.nav_chat) {
                    intent = new Intent(MainActivity.this, ChatImplementation.class);
                    toolbar.setTitle("Chat");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
