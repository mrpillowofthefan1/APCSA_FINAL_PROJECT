package com.example.apcsa_final_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.ViewHolder> {

    private final List<MarketObjects> marketItems;
    private static final String BASE_URL = NetworkConfig.BASE_URL;

    public MarketAdapter(List<MarketObjects> marketItems) {
        this.marketItems = marketItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_market, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MarketObjects item = marketItems.get(position);
        
        holder.itemName.setText(item.getName());
        holder.itemPrice.setText(item.getFormattedPrice());
        holder.itemDescription.setText(item.getDescription());
        holder.itemUsername.setText("by " + item.getUsername());
        
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            String fullImageUrl = BASE_URL + item.getImagePath();
            Glide.with(holder.itemView.getContext())
                    .load(fullImageUrl)
                    .placeholder(item.getImageResourceId())
                    .error(item.getImageResourceId())
                    .centerCrop()
                    .into(holder.itemImage);
        } else {
            holder.itemImage.setImageResource(item.getImageResourceId());
        }
        
        holder.itemSpecificDetails.setText(item.getSpecificDetails());

        View.OnClickListener checkoutListener = v -> {
            item.launchCheckout(v.getContext());
        };

        holder.itemView.setOnClickListener(checkoutListener);
        holder.buyButton.setOnClickListener(checkoutListener);
    }

    @Override
    public int getItemCount() {
        return marketItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView itemImage;
        public final TextView itemName;
        public final TextView itemPrice;
        public final TextView itemDescription;
        public final TextView itemUsername;
        public final TextView itemSpecificDetails;
        public final Button buyButton;

        public ViewHolder(View view) {
            super(view);
            itemImage = view.findViewById(R.id.item_image);
            itemName = view.findViewById(R.id.item_name);
            itemPrice = view.findViewById(R.id.item_price);
            itemDescription = view.findViewById(R.id.item_description);
            itemUsername = view.findViewById(R.id.item_username);
            itemSpecificDetails = view.findViewById(R.id.item_specific_details);
            buyButton = view.findViewById(R.id.buy_button);
        }
    }
}
