package com.example.sifre_app;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messages;
    private EditText messageInput;
    private ImageButton sendButton;
    private FirebaseFirestore db;
    private String chatRoomId;
    private String currentUserId;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRoomId = getIntent().getStringExtra("chatRoomId");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        setupToolbar();
        initializeViews();
        setupRecyclerView();
        loadMessages();
        setupSendButton();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbarTitle = findViewById(R.id.toolbarTitle);
        loadChatRoomName();
    }

    private void loadChatRoomName() {
        db.collection("chatRooms")
                .document(chatRoomId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String chatName = documentSnapshot.getString("name");
                        if (chatName != null) {
                            toolbarTitle.setText(chatName);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Sohbet bilgileri yüklenemedi", Toast.LENGTH_SHORT).show();
                });
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        messages = new ArrayList<>();
    }

    private void setupRecyclerView() {
        messageAdapter = new MessageAdapter(messages, currentUserId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);
    }

    private void loadMessages() {
        db.collection("chatRooms")
                .document(chatRoomId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Mesajlar yüklenirken hata oluştu", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    messages.clear();
                    for (DocumentSnapshot document : value.getDocuments()) {
                        Message message = document.toObject(Message.class);
                        if (message != null) {
                            messages.add(message);
                        }
                    }
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messages.size() - 1);
                });
    }

    private void setupSendButton() {
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                messageInput.setText("");
            }
        });
    }

    private void sendMessage(String messageText) {
        String encryptedText = SimpleCipher.encrypt(messageText);
        Map<String, Object> message = new HashMap<>();
        message.put("text", encryptedText);
        message.put("senderId", currentUserId);
        message.put("timestamp", System.currentTimeMillis());

        db.collection("chatRooms")
                .document(chatRoomId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    updateLastMessage(encryptedText);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Mesaj gönderilemedi", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateLastMessage(String encryptedText) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("lastMessage", encryptedText);
        updates.put("lastMessageTimestamp", System.currentTimeMillis());
        updates.put("lastMessageSenderId", currentUserId);

        db.collection("chatRooms")
                .document(chatRoomId)
                .update(updates)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Son mesaj güncellenemedi", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Temizlik işlemleri
        recyclerView = null;
        messageAdapter = null;
        messages = null;
        messageInput = null;
        sendButton = null;
        toolbarTitle = null;
    }
}
