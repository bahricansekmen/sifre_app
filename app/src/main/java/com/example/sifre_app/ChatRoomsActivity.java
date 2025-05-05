package com.example.sifre_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatRoomAdapter adapter;
    private ArrayList<ChatRoom> chatRooms;
    private FirebaseFirestore db;
    private String currentUserId;
    private FloatingActionButton createChatButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_rooms);

        // Kullanıcı oturumu kontrolü
        com.google.firebase.auth.FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // Kullanıcı giriş yapmamış, giriş ekranına yönlendir
            Intent intent = new Intent(this, MainActivity.class); // veya LoginActivity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }
        currentUserId = user.getUid();
        db = FirebaseFirestore.getInstance();

        setupToolbar();
        initializeViews();
        setupRecyclerView();
        setupCreateChatButton();
        setupSwipeRefresh();
        loadChatRooms();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sohbet Odaları");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        createChatButton = findViewById(R.id.createChatButton);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        chatRooms = new ArrayList<>();
    }

    private void setupRecyclerView() {
        adapter = new ChatRoomAdapter(chatRooms, this::onChatRoomClick);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        // Ayırıcı çizgi ekle
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                layoutManager.getOrientation()
        );
        recyclerView.addItemDecoration(dividerItemDecoration);
        
        recyclerView.setAdapter(adapter);
    }

    private void setupCreateChatButton() {
        createChatButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateChatActivity.class);
            startActivity(intent);
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Yenileme sırasında verileri tekrar yükle
            loadChatRooms();
        });
    }

    private void loadChatRooms() {
        db.collection("chatRooms")
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (!task.isSuccessful() || task.getResult() == null) {
                        Toast.makeText(this, "Sohbet odaları yüklenirken hata oluştu", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    chatRooms.clear();
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        ChatRoom chatRoom = new ChatRoom();
                        chatRoom.setId(document.getId());
                        chatRoom.setName(document.getString("name"));
                        chatRoom.setMembers((List<String>) document.get("members"));
                        chatRoom.setLastMessage(document.getString("lastMessage"));
                        chatRoom.setLastMessageSenderId(document.getString("lastMessageSenderId"));
                        Object ts = document.get("lastMessageTimestamp");
                        if (ts instanceof Long) {
                            chatRoom.setLastMessageTimestamp(new java.util.Date((Long) ts));
                        } else if (ts instanceof java.util.Date) {
                            chatRoom.setLastMessageTimestamp((java.util.Date) ts);
                        } else {
                            chatRoom.setLastMessageTimestamp(null);
                        }
                        chatRooms.add(chatRoom);
                    }
                    adapter.updateChatRooms(chatRooms);
                });
    }

    private void onChatRoomClick(ChatRoom chatRoom) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("chatRoomId", chatRoom.getId());
        startActivity(intent);
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
        adapter = null;
        chatRooms = null;
        createChatButton = null;
        swipeRefreshLayout = null;
    }
} 