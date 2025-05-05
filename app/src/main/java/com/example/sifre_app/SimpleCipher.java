package com.example.sifre_app;

public class SimpleCipher {
    private static final int SHIFT = 3; // Kaç harf kaydırılacak

    public static String encrypt(String plainText) {
        StringBuilder sb = new StringBuilder();
        for (char c : plainText.toCharArray()) {
            sb.append((char) (c + SHIFT));
        }
        return sb.toString();
    }

    public static String decrypt(String cipherText) {
        StringBuilder sb = new StringBuilder();
        for (char c : cipherText.toCharArray()) {
            sb.append((char) (c - SHIFT));
        }
        return sb.toString();
    }
} 