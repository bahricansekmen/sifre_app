package com.example.sifre_app;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class SifreApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Firebase'i başlat
        FirebaseApp.initializeApp(this);
        
        // Firebase Database'i çevrimdışı kullanım için etkinleştir
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        
        // Firebase Database önbelleğini etkinleştir
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
    }
} 