package com.example.sifre_app;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SifreCipher {
    private static final String ALGORITHM = "AES";
    private static final String KEY = "BuBirGizliAnahtar"; // Gerçek uygulamada güvenli bir şekilde saklanmalı

    public static String encrypt(String value) {
        try {
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
            return Base64.getEncoder().encodeToString(encryptedByteValue);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String value) {
        try {
            SecretKeySpec key = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedValue64 = Base64.getDecoder().decode(value);
            byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
            return new String(decryptedByteValue, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 