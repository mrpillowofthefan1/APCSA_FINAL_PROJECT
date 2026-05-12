package com.example.apcsa_final_project;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// adapter for thread comments
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private final List<CommentObject> comments;

    public CommentAdapter(List<CommentObject> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommentObject comment = comments.get(position);
        holder.username.setText(comment.getUsername());
        holder.role.setText(String.format("(%s)", comment.getRole()));
        holder.content.setText(comment.getContent());
        holder.date.setText(comment.getCreatedAt());

        // color role based on if farmer or user
        if ("Farmer".equalsIgnoreCase(comment.getRole())) {
            holder.role.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.role.setTextColor(Color.parseColor("#2196F3"));
        }
    }

    @Override
    public int getItemCount() { return comments.size(); }

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
