package com.example.sifre_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity {
    private EditText nameText, emailText, passwordText, passwordControlText;
    private Button kayitOlButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        passwordControlText = findViewById(R.id.passwordControlText);
        kayitOlButton = findViewById(R.id.kayitOlButton);
    }

    private void setupClickListeners() {
        kayitOlButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = nameText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String passwordControl = passwordControlText.getText().toString().trim();

        if (!validateInputs(name, email, password, passwordControl)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        saveUserData(name, email);
                    } else {
                        showError(task.getException().getMessage());
                    }
                });
    }

    private boolean validateInputs(String name, String email, String password, String passwordControl) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordControl.isEmpty()) {
            Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(passwordControl)) {
            Toast.makeText(this, "Şifreler eşleşmiyor", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveUserData(String name, String email) {
        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("createdAt", System.currentTimeMillis());

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> showSuccess())
                .addOnFailureListener(e -> showError(e.getMessage()));
    }

    private void showSuccess() {
        Toast.makeText(this, "Kayıt başarılı", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        Toast.makeText(this, "Kayıt başarısız: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Temizlik işlemleri
        nameText = null;
        emailText = null;
        passwordText = null;
        passwordControlText = null;
        kayitOlButton = null;
    }
}