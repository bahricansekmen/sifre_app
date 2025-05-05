package com.example.sifre_app;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class ChatRoom {
    @Exclude
    private String id;
    private String name;
    private List<String> members;
    private String lastMessage;
    private String lastMessageSenderId;
    @ServerTimestamp
    private Date lastMessageTimestamp;

    public ChatRoom() {
        // Firestore için boş constructor gerekli
    }

    public ChatRoom(String name, List<String> members) {
        this.name = name;
        this.members = members;
    }

    @Exclude
    public String getId() {
        return id;
    }

    @Exclude
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageSenderId() {
        return lastMessageSenderId;
    }

    public void setLastMessageSenderId(String lastMessageSenderId) {
        this.lastMessageSenderId = lastMessageSenderId;
    }

    public Date getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Date lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }
} 