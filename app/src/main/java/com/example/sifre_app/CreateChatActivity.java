package com.example.sifre_app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateChatActivity extends AppCompatActivity {
    private EditText chatNameInput;
    private EditText otherUserEmailInput;
    private Button createButton;
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        setupToolbar();
        initializeViews();
        setupCreateButton();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Yeni Sohbet");
        }
    }

    private void initializeViews() {
        chatNameInput = findViewById(R.id.chatNameInput);
        otherUserEmailInput = findViewById(R.id.otherUserEmailInput);
        createButton = findViewById(R.id.createButton);
    }

    private void setupCreateButton() {
        createButton.setOnClickListener(v -> {
            String chatName = chatNameInput.getText().toString().trim();
            String otherUserEmail = otherUserEmailInput.getText().toString().trim();
            if (chatName.isEmpty() || otherUserEmail.isEmpty()) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            findUserIdByEmailAndCreateChat(chatName, otherUserEmail);
        });
    }

    private void findUserIdByEmailAndCreateChat(String chatName, String email) {
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    String otherUserId = queryDocumentSnapshots.getDocuments().get(0).getId();
                    createChatRoom(chatName, otherUserId);
                } else {
                    Toast.makeText(this, "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Kullanıcı aranırken hata oluştu", Toast.LENGTH_SHORT).show();
            });
    }

    private void createChatRoom(String chatName, String otherUserId) {
        List<String> members = new ArrayList<>();
        members.add(currentUserId);
        members.add(otherUserId);

        Map<String, Object> chatRoom = new HashMap<>();
        chatRoom.put("name", chatName);
        chatRoom.put("members", members);
        chatRoom.put("lastMessageTimestamp", 0);

        db.collection("chatRooms")
            .add(chatRoom)
            .addOnSuccessListener(documentReference -> {
                Toast.makeText(this, "Sohbet odası oluşturuldu", Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Sohbet odası oluşturulamadı", Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Temizlik işlemleri
        chatNameInput = null;
        otherUserEmailInput = null;
        createButton = null;
    }
} 