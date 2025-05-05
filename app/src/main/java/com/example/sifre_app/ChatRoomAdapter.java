package com.example.sifre_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder> {
    private ArrayList<ChatRoom> chatRooms;
    private OnChatRoomClickListener listener;

    public interface OnChatRoomClickListener {
        void onChatRoomClick(ChatRoom chatRoom);
    }

    public ChatRoomAdapter(ArrayList<ChatRoom> chatRooms, OnChatRoomClickListener listener) {
        this.chatRooms = chatRooms;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_room, parent, false);
        return new ChatRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position) {
        ChatRoom chatRoom = chatRooms.get(position);
        holder.bind(chatRoom);
    }

    @Override
    public int getItemCount() {
        return chatRooms.size();
    }

    public void updateChatRooms(ArrayList<ChatRoom> newChatRooms) {
        this.chatRooms = newChatRooms;
        notifyDataSetChanged();
    }

    class ChatRoomViewHolder extends RecyclerView.ViewHolder {
        private TextView chatNameTextView;
        private TextView lastMessageTextView;
        private TextView timestampTextView;

        public ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            chatNameTextView = itemView.findViewById(R.id.chatNameTextView);
            lastMessageTextView = itemView.findViewById(R.id.lastMessageTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onChatRoomClick(chatRooms.get(position));
                }
            });
        }

        public void bind(ChatRoom chatRoom) {
            chatNameTextView.setText(chatRoom.getName());

            String lastMessage = chatRoom.getLastMessage();
            if (lastMessage != null && !lastMessage.isEmpty()) {
                lastMessageTextView.setText(lastMessage);
                lastMessageTextView.setVisibility(View.VISIBLE);
            } else {
                lastMessageTextView.setVisibility(View.GONE);
            }

            Date timestamp = chatRoom.getLastMessageTimestamp();
            if (timestamp != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                timestampTextView.setText(sdf.format(timestamp));
                timestampTextView.setVisibility(View.VISIBLE);
            } else {
                timestampTextView.setVisibility(View.GONE);
            }
        }
    }
} 