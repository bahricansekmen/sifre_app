package com.example.sifre_app.data;

public class Message {
    private String id;
    private String senderId;
    private String receiverId;
    private String text;
    private long timestamp;

    public Message() {
        // Firebase için boş constructor
    }

    public Message(String id, String senderId, String receiverId, String text, long timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }
} 