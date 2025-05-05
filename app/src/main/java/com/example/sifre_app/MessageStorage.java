package com.example.sifre_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.sifre_app.data.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.example.sifre_app.encryption.SifreCipher;

public class MessageStorage {
    private static final String TAG = "MessageStorage";
    private static final String PREF_NAME = "message_storage";
    private static final String ENCRYPTION_PASSWORD = "SifreApp2024Secret";
    
    private final Context context;
    private final Gson gson;
    
    public MessageStorage(Context context) {
        this.context = context.getApplicationContext();
        this.gson = new Gson();
    }
    
    public void saveMessage(String roomId, Message message) {
        try {
            // Mesajı JSON'a çevir
            String messageJson = gson.toJson(message);
            
            // Şifrele
            String encryptedMessage = SifreCipher.encrypt(messageJson);
            
            // Şifrelenmiş mesajı kaydet
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String key = "message_" + roomId + "_" + message.getTimestamp();
            prefs.edit().putString(key, encryptedMessage).apply();
            
        } catch (Exception e) {
            Log.e(TAG, "Mesaj kaydedilirken hata: " + e.getMessage());
        }
    }
    
    public List<Message> getMessages(String roomId) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            List<Message> messages = new ArrayList<>();
            
            // Odaya ait tüm mesajları bul
            Map<String, ?> allEntries = prefs.getAll();
            String prefix = "message_" + roomId + "_";
            
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                if (entry.getKey().startsWith(prefix)) {
                    String encryptedMessage = (String) entry.getValue();

                    Log.e(TAG, encryptedMessage);

                    // Şifreyi çöz
                    String decryptedJson = SifreCipher.decrypt(encryptedMessage);
                    
                    // JSON'dan Message nesnesine çevir
                    Message message = gson.fromJson(decryptedJson, Message.class);
                    messages.add(message);
                }
            }
            
            // Mesajları zamana göre sırala
            Collections.sort(messages, (m1, m2) -> 
                Long.compare(m1.getTimestamp(), m2.getTimestamp()));
                
            return messages;
            
        } catch (Exception e) {
            Log.e(TAG, "Mesajlar alınırken hata: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public void addMessage(String roomId, Message message) {
        try {
            List<Message> messages = getMessages(roomId);
            messages.add(message);
            saveMessage(roomId, message);
            
            Log.d(TAG, "Yeni mesaj eklendi, toplam: " + messages.size());
        } catch (Exception e) {
            Log.e(TAG, "Mesaj eklenirken hata: " + e.getMessage(), e);
        }
    }
} 