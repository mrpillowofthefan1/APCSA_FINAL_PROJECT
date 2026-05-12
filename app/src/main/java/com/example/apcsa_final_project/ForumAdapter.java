package com.example.apcsa_final_project;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// adapter for forum thread list
public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ViewHolder> {
    private final List<PostObject> posts;

    public ForumAdapter(List<PostObject> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostObject post = posts.get(position);
        holder.username.setText(post.getUsername());
        holder.role.setText(String.format("(%s)", post.getRole()));
        holder.content.setText(post.getTitle());
        holder.date.setText(post.getCreatedAt());

        // set role color based on type
        if ("Farmer".equalsIgnoreCase(post.getRole())) {
            holder.role.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.role.setTextColor(Color.parseColor("#2196F3"));
        }

        // click to open thread details
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ForumThreadActivity.class);
            intent.putExtra("THREAD_ID", post.getId());
            intent.putExtra("THREAD_TITLE", post.getTitle());
            
            if (v.getContext() instanceof Forum) {
                Forum activity = (Forum) v.getContext();
                intent.putExtra("ROLE", activity.getIntent().getStringExtra("ROLE"));
                intent.putExtra("DISPLAY_NAME", activity.getIntent().getStringExtra("DISPLAY_NAME"));
            }
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return posts.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView username, role, content, date;
        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.post_username);
            role = itemView.findViewById(R.id.post_role);
            content = itemView.findViewById(R.id.post_content);
            date = itemView.findViewById(R.id.post_date);
        }
    }
}
