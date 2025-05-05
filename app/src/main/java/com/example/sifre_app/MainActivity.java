package com.example.sifre_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sifre_app.data.User;
import com.example.sifre_app.data.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private Button loginButton, registerButton;
    private EditText nameText, passwordText;
    private UserRepository userRepository;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Repository başlatma
        userRepository = new UserRepository(getApplication());
        mAuth = FirebaseAuth.getInstance();

        // UI bileşenlerini bulma
        loginButton = findViewById(R.id.kayıtOlButton);
        registerButton = findViewById(R.id.registerButton);
        nameText = findViewById(R.id.nameText);
        passwordText = findViewById(R.id.passwordText);

        // Giriş düğmesi tıklama olayı
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String email = nameText.getText().toString().trim();
                    String password = passwordText.getText().toString().trim();

                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Kullanıcı adı ve şifre gerekli!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Giriş başarılı!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, ChatRoomsActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "Giriş başarısız: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                } catch (Exception e) {
                    Log.e("MainActivity", "Giriş işleminde hata: " + e.getMessage(), e);
                    Toast.makeText(MainActivity.this, "Hata: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Kayıt düğmesi tıklama olayı
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });
    }
}