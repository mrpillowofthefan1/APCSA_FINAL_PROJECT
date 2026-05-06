package com.example.apcsa_final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import org.jspecify.annotations.NonNull;

public class StartGuide extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private CardView card1, card2, card3, card4, card5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_guide);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        
        card1 = findViewById(R.id.card_view_1);
        card2 = findViewById(R.id.card_view_2);
        card3 = findViewById(R.id.card_view_3);
        card4 = findViewById(R.id.card_view_4);
        card5 = findViewById(R.id.card_view_5);

        toolbar.setTitle("Start Guide");

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    startActivity(new Intent(StartGuide.this, Home.class));
                } else if (item.getItemId() == R.id.nav_forum) {
                    startActivity(new Intent(StartGuide.this, Forum.class));
                } else if (item.getItemId() == R.id.nav_market) {
                    startActivity(new Intent(StartGuide.this, Market.class));
                } else if (item.getItemId() == R.id.nav_chat) {
                    startActivity(new Intent(StartGuide.this, ChatImplementation.class));
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        card1.setOnClickListener(v -> showDetail("Starting a Farm Business",
            "Making a farm business can be an extensive and hard process. This guide is here to help streamline the process.\n\n" +
            "Step 1: It is important to understand your reasons for farming. Farming is tough, and figuring out why you want to farm is a good motivator to keep farming.\n" +
            "(Example) – I want to farm to provide for my family and make some extra money.\n\n" +
            "Step 2: Figure out your model. This is very important for the planning and logistical parts of farmwork. Figure out whether you want to do this full-time or part-time. Figure out what you plan to grow. Figure out where you want to start to grow your product. Figure out whether you want to expand in the future and prepare for that.\n" +
            "(Example) – I want to farm part-time, making potatoes in my backyard, and I plan on adding a couple more planters in the future to grow radishes.\n\n" +
            "Step 3: Figure out the financial aspect. Figure out if you want to keep it for yourself or to make it under a business. Make sure to figure out costs, taxes, and which bank you plan to use if you are making a whole business. Remember that the start is the hardest part, and plan for an initial net loss.\n" +
            "(Example) – I plan to be the sole owner, so I do not need an LLC or anything. I plan to buy equipment and seeds within a $10 budget. I will use my personal bank account. I have savings for the first year.\n\n" +
            "Step 4: Get started on farming. Make sure to use all the resources that government agencies can provide. It's important to start building connections to get clients. Whether you use our app or go forth on your own is up to you.\n" +
            "(Example) – I start planting my potatoes. Unfortunately, I cannot get a government loan for starting farmers, but it is worth keeping in mind. I will contact local businesses to see if they need any of my potatoes, and also use the app to make more connections."));

        card2.setOnClickListener(v -> showDetail("Basic Plant Guide",
            "The most important part of a farm is what you're farming. This guide provides general guidelines for getting started in farming.\n\n" +
            "Step 1: Prep the ideal location for your plants. It should have the recommended sunlight for that plant. It should not be in an overly wet place or a toxic environment. It is important to consider whether you want it indoors or outdoors, and if you want it close to you so you do not forget about it.\n" +
            "(Example) – I will grow tomatoes outside to get their sunlight, but next to the porch so it gets shade, and I pass by it every day. It does not have any leaks or anything that could affect it from the outside.\n\n" +
            "Step 2: It is important to get good soil. Most stores have good-quality soil, and make sure to get good soil for your plants. Make sure to get fertilizer with that, whether it comes with the solid or as an extra step.\n" +
            "(Example) – I will get good potting soil at my local Home Depot that has fertilizer in it.\n\n" +
            "Step 3: It is now important that the plant growth has been planned.\n" +
            "(Example) – I have a good 4x4 planter for my tomatoes. I live in Arizona, and the growing period for tomatoes is from February to June and from August to November.\n\n" +
            "Step 4: Provide continuous care for your plants. It is good to water your plants correctly; most need a good, deep watering once, not multiple light sprinkles. It is also good to make sure weeds are taken care of before they grow out of control.\n" +
            "(Example) – I water my tomatoes in the morning every day, and every Saturday and Wednesday, I eliminate all weeds."));

        card3.setOnClickListener(v -> showDetail("How to Use the App",
            "Whether it is your first time or you need a refresher, here is a guide to help you learn how to navigate the app.\n\n" +
            "• Home – home screen that contains all the necessary information that you need\n" +
            "• Forum – a place to connect with other users. It can be used to offer information and to meet people who want to make a deal\n" +
            "• Start Guide – guides to get started using the app and farming\n" +
            "• Market – a place to make deals, whether with new people or old clients. This is where the money exchanges hands using Stripe\n" +
            "• Chat – Private chats with people who are interested in making a deal"));

        card4.setOnClickListener(v -> showDetail("How to Buy/Sell",
            "One aspect of the app is that it directly connects you with people who want to make a deal with you. Here is a guide to help you with that process.\n\n" +
            "Step 1: Identify whether you want to buy or sell produce. Then, search the market for people who might be interested. If not, you can go to the forum and ask if anyone wants what you're interested in.\n" +
            "(Example) I have a lot of pumpkins that I want to sell, so I searched the market for anyone who might want pumpkins, and since I made an offer for my pumpkins\n\n" +
            "Step 2: Once you find someone, the app connects your payments using Stripe, and your deal is finalized.\n" +
            "(Example) I found someone who wanted my pumpkins, so we talked about a good price in the chat. Once we agreed, we made the transaction, and everyone was happy.\n\n" +
            "Step 3: If you want to reconnect with someone, you can use the Chat to find someone you met before. Otherwise, you’ve learned how to buy/sell on the app.\n" +
            "(Example) I had more pumpkins and connected with the previous buyer to see if he wanted to buy more. Now, they regularly buy pumpkins from me."));

        card5.setOnClickListener(v -> showDetail("Farming Beginnings",
            "Farming can be hard to get into. Here is a guide to help you learn the processes from multiple angles.\n\n" +
            "Step 1: Check out our other start guides. They provide a general overview of aspects of the farm business that can help you start farming.\n" +
            "(Example) I do not know how to plant things, so I head over to the Basic Plant Guide\n\n" +
            "Step 2: Contact people through the Forum and the Chat. These places are meant for public and private conversations about farming.\n" +
            "(Example) I met some people on the Forum who gave me a specific example of how to farm basil. Now we have a private Chat so that they can give me specific advice without clogging up the forum.\n\n" +
            "Step 3: Outside of the app, there are many resources to learn about farming. However, it is important to narrow down your search as many guides on the internet are not for you. Often, local businesses and farmers will be your best bet on learning to farm. They often have the basic knowledge about what you want and they will also have region specific knowledge that can help you learn.\n" +
            "(Example) I, a New Yorker, go down to my local city gardeners. They give me knowledge about how to grow basil in a big city that has problems that other places do not have."));

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

    private void showDetail(String title, String content) {
        StartGuideDialogFragment dialog = StartGuideDialogFragment.newInstance(title, content);
        dialog.show(getSupportFragmentManager(), "StartGuideDetail");
    }
}