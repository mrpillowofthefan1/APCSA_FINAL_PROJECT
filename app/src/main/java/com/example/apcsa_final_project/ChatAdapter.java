package com.example.apcsa_final_project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        if (message.isUser()) {
            holder.textUser.setVisibility(View.VISIBLE);
            holder.textBot.setVisibility(View.GONE);
            holder.textUser.setText(message.getMessage());
        } else {
            holder.textBot.setVisibility(View.VISIBLE);
            holder.textUser.setVisibility(View.GONE);
            holder.textBot.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textUser;
        TextView textBot;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textUser = itemView.findViewById(R.id.text_message_user);
            textBot = itemView.findViewById(R.id.text_message_bot);
        }
    }
}