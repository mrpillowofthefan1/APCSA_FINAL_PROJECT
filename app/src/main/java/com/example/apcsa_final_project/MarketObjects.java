package com.example.apcsa_final_project;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

// base class for all market items
public abstract class MarketObjects {
    private final int id;
    private final String name;
    private final String price;
    private final String description;
    private final String username;
    private final String imagePath;

    private final int imageResourceId;
    private final String stripePriceId;

    // constructor for setting up product data
    public MarketObjects(int id, String name, String price, String description, String username, int imageResourceId, String stripePriceId, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.username = username;
        this.imageResourceId = imageResourceId;
        this.stripePriceId = stripePriceId;
        this.imagePath = imagePath;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getDescription() { return description; }
    public String getUsername() { return username; }
    public String getImagePath() { return imagePath; }

    public int getImageResourceId() { return imageResourceId; }
    public String getStripePriceId() { return stripePriceId; }

    // converts string price to cents for stripe
    public long getPriceInCents() {
        try {
            double numericPrice = Double.parseDouble(price.replaceAll("[^\\d.]", ""));
            return (long) (numericPrice * 100);
        } catch (Exception e) {
            return 0;
        }
    }

    // abstract method for child classes to implement
    public abstract String getSpecificDetails();

    public String getFormattedPrice() {
        return "$" + price;
    }

    @Override
    public String toString() {
        return name + " (by " + username + ") - " + getFormattedPrice();
    }

    // starts stripe checkout screen
    public void launchCheckout(android.content.Context context) {
        android.content.Intent intent = new android.content.Intent(context, CheckoutActivity.class);
        intent.putExtra("ITEM_NAME", getName());
        intent.putExtra("ITEM_PRICE_CENTS", getPriceInCents());
        intent.putExtra("STRIPE_PRICE_ID", getStripePriceId());
        context.startActivity(intent);
    }

    // creates specific product objects from json data
    public static MarketObjects fromJson(JSONObject json) {
        try {
            int id = json.getInt("id");
            String name = json.getString("item_name");
            String price = json.getString("price");
            String desc = json.optString("description", "");
            String user = json.getString("username");
            String type = json.optString("type", "Plant");
            String detail = json.optString("specific_detail", "");
            String imgPath = json.optString("image_path", "");

            int imgRes = R.drawable.plant;
            if ("Tool".equalsIgnoreCase(type)) imgRes = R.drawable.farm_business;

            // switch case to decide which subclass to make
            switch (type) {
                case "Tool": return new ToolProduct(id, name, price, desc, user, imgRes, "price_tool", detail, imgPath);
                case "Seed": return new SeedProduct(id, name, price, desc, user, imgRes, "price_seed", 100, imgPath);
                case "Produce": return new ProduceProduct(id, name, price, desc, user, imgRes, "price_produce", detail, imgPath);
                default: return new PlantProduct(id, name, price, desc, user, imgRes, "price_plant", detail, imgPath);
            }
        } catch (Exception e) {
            return null;
        }
    }

    // for loop to sum up all prices in the list
    public static long calculateTotalMarketValue(List<MarketObjects> items) {
        long totalValue = 0;
        for (MarketObjects item : items) {
            totalValue += item.getPriceInCents();
        }
        return totalValue;
    }

    public static List<MarketObjects> getMarketItems() {
        List<MarketObjects> items = new ArrayList<>();
        items.add(new PlantProduct(1, "Tomato Plant", "15.00", "Organic heirloom tomato plant", "FarmerJohn", R.drawable.plant, "price_tomato_123", "Seedling", ""));
        return items;
    }
}

// plant subclass
class PlantProduct extends MarketObjects {
    private final String growthStage;
    public PlantProduct(int id, String name, String price, String description, String username, int imageResourceId, String stripePriceId, String growthStage, String imagePath) {
        super(id, name, price, description, username, imageResourceId, stripePriceId, imagePath);
        this.growthStage = growthStage;
    }
    @Override public String getSpecificDetails() { return "Growth Stage: " + growthStage; }
}

// tool subclass
class ToolProduct extends MarketObjects {
    private final String category;
    public ToolProduct(int id, String name, String price, String description, String username, int imageResourceId, String stripePriceId, String category, String imagePath) {
        super(id, name, price, description, username, imageResourceId, stripePriceId, imagePath);
        this.category = category;
    }
    @Override public String getSpecificDetails() { return "Tool Category: " + category; }
}

// seed subclass
class SeedProduct extends MarketObjects {
    private final int packetQuantity;
    public SeedProduct(int id, String name, String price, String description, String username, int imageResourceId, String stripePriceId, int packetQuantity, String imagePath) {
        super(id, name, price, description, username, imageResourceId, stripePriceId, imagePath);
        this.packetQuantity = packetQuantity;
    }
    @Override public String getSpecificDetails() { return "Seeds per packet: " + packetQuantity; }
}

// produce subclass
class ProduceProduct extends MarketObjects {
    private final String harvestFrequency;
    public ProduceProduct(int id, String name, String price, String description, String username, int imageResourceId, String stripePriceId, String harvestFrequency, String imagePath) {
        super(id, name, price, description, username, imageResourceId, stripePriceId, imagePath);
        this.harvestFrequency = harvestFrequency;
    }
    @Override public String getSpecificDetails() { return "Harvested: " + harvestFrequency; }
}
