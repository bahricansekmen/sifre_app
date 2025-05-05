package com.example.sifre_app.data;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.auth.FirebaseAuth;

public class ChatService {
    private static ChatService instance;
    private final DatabaseReference databaseReference;
    private final FirebaseAuth auth;

    private ChatService() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
    }

    public static ChatService getInstance() {
        if (instance == null) {
            instance = new ChatService();
        }
        return instance;
    }

    public void sendMessage(String receiverId, String content, boolean isEncrypted) {
        String currentUserId = auth.getCurrentUser().getUid();
        String chatId = getChatId(currentUserId, receiverId);
        String messageId = databaseReference.child("chats").child(chatId).push().getKey();
        
        Message message = new Message(
            messageId,
            currentUserId,
            receiverId,
            content,
            System.currentTimeMillis()
        );
        
        // Mesajı gönderen ve alıcının mesaj listelerine ekle
        databaseReference.child("chats").child(chatId).child(messageId).setValue(message);
        
        // Kullanıcıların mesaj listelerine ekle
        databaseReference.child("user_messages")
                .child(currentUserId)
                .child(receiverId)
                .child(messageId)
                .setValue(true);
                
        databaseReference.child("user_messages")
                .child(receiverId)
                .child(currentUserId)
                .child(messageId)
                .setValue(true);
    }

    private String getChatId(String userId1, String userId2) {
        return userId1.compareTo(userId2) < 0 ? 
               userId1 + "_" + userId2 : 
               userId2 + "_" + userId1;
    }

    public void listenForMessages(String otherUserId, MessageListener listener) {
        String currentUserId = auth.getCurrentUser().getUid();
        String chatId = getChatId(currentUserId, otherUserId);
        
        databaseReference.child("chats").child(chatId)
                .orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Message message = snapshot.getValue(Message.class);
                            if (message != null) {
                                listener.onMessageReceived(message);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onError(databaseError.getMessage());
                    }
                });
    }

    public interface MessageListener {
        void onMessageReceived(Message message);
        void onError(String error);
    }
} 