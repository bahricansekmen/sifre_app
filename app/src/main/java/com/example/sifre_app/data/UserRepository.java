package com.example.sifre_app.data;

import android.app.Application;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private DatabaseReference databaseReference;

    public UserRepository(Application application) {
        try {
            databaseReference = FirebaseDatabase.getInstance().getReference("users");
            Log.d(TAG, "UserRepository başlatıldı");
        } catch (Exception e) {
            Log.e(TAG, "Veritabanı başlatılırken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Kullanıcı ekleme
    public void insert(User user) {
        try {
            databaseReference.child(user.getUsername()).setValue(user);
            Log.d(TAG, "Kullanıcı eklendi: " + user.getUsername());
        } catch (Exception e) {
            Log.e(TAG, "Kullanıcı eklenirken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Kullanıcı adı ve şifre ile giriş doğrulama
    public CompletableFuture<User> loginUser(String username, String password) {
        CompletableFuture<User> future = new CompletableFuture<>();
        
        databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null && user.getPassword().equals(password)) {
                    future.complete(user);
                } else {
                    future.complete(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        
        return future;
    }

    // Kullanıcı adı kontrolü
    public CompletableFuture<Boolean> isUsernameTaken(String username) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        databaseReference.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                future.complete(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        
        return future;
    }

    // E-posta kontrolü (kayıt sırasında)
    public CompletableFuture<Boolean> isEmailTaken(String email) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getEmail().equals(email)) {
                        future.complete(true);
                        return;
                    }
                }
                future.complete(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        
        return future;
    }

    // Tüm kullanıcıları getir
    public CompletableFuture<List<User>> getAllUsers() {
        CompletableFuture<List<User>> future = new CompletableFuture<>();
        
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        users.add(user);
                    }
                }
                future.complete(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        
        return future;
    }
} 