package com.example.apcsa_final_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Market extends AppCompatActivity {
    private static final String TAG = "Market";
    private static final String GET_PRODUCTS_URL = NetworkConfig.BASE_URL + "get_products.php";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MarketAdapter adapter;
    private List<MarketObjects> marketItems = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_market);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            return insets;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.market_recycler_view);

        toolbar.setTitle("Market");
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        
        adapter = new MarketAdapter(marketItems);
        recyclerView.setAdapter(adapter);

        fetchProducts();

        com.google.android.material.floatingactionbutton.FloatingActionButton fab = findViewById(R.id.fab_add_product);
        String role = getIntent().getStringExtra("ROLE");
        String displayName = getIntent().getStringExtra("DISPLAY_NAME");

        if ("Farmer".equalsIgnoreCase(role)) {
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(v -> {
                AddProductDialogFragment dialog = AddProductDialogFragment.newInstance(displayName);
                dialog.show(getSupportFragmentManager(), "AddProduct");
            });
        } else {
            fab.setVisibility(View.GONE);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            Intent intent = null;
            if (item.getItemId() == R.id.nav_home) {
                intent = new Intent(Market.this, Home.class);
            } else if (item.getItemId() == R.id.nav_forum) {
                intent = new Intent(Market.this, Forum.class);
            } else if (item.getItemId() == R.id.nav_start_guide) {
                intent = new Intent(Market.this, StartGuide.class);
            } else if (item.getItemId() == R.id.nav_market) {
            } else if (item.getItemId() == R.id.nav_chat) {
                intent = new Intent(Market.this, ChatImplementation.class);
            }

            if (intent != null) {
                intent.putExtra("ROLE", getIntent().getStringExtra("ROLE"));
                intent.putExtra("DISPLAY_NAME", getIntent().getStringExtra("DISPLAY_NAME"));
                startActivity(intent);
            }
            drawerLayout.closeDrawers();
            return true;
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

    // grabs all products from the php server
    public void fetchProducts() {
        Request request = new Request.Builder().url(GET_PRODUCTS_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(Market.this, "Failed to load items", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    try {
                        JSONArray array = new JSONArray(jsonData);
                        marketItems.clear();
                        // loop through results to make objects using polymorphism
                        for (int i = 0; i < array.length(); i++) {
                            MarketObjects obj = MarketObjects.fromJson(array.getJSONObject(i));
                            if (obj != null) marketItems.add(obj);
                        }
                        
                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            // calc total value for apcsa requirements
                            long totalValue = MarketObjects.calculateTotalMarketValue(marketItems);
                            Toast.makeText(Market.this, "Total items value: $" + (totalValue / 100.0), Toast.LENGTH_SHORT).show();
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
