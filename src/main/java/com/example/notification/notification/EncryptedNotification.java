package com.example.notification.notification;

import com.example.notification.sender.MessageSender;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptedNotification extends Notification {

    private final SecretKeySpec secretKey;
    private final SecureRandom random = new SecureRandom();

    public EncryptedNotification(MessageSender sender, byte[] key) {
        super(sender);

        if (key == null || key.length != 32) {
            throw new IllegalArgumentException(
                    "AES key must contain exactly 32 bytes"
            );
        }

        this.secretKey = new SecretKeySpec(key.clone(), "AES");
    }

    @Override
    public boolean send(String recipient, String title, String body) {
        try {
            String encryptedTitle = encrypt(title);
            String encryptedBody = encrypt(body);

            return sender.isAvailable()
                    && sender.sendMessage(
                            recipient,
                            encryptedTitle,
                            encryptedBody
                    );
        } catch (Exception e) {
            System.err.println(
                    "Failed to encrypt notification: " + e.getMessage()
            );
            return false;
        }
    }

    private String encrypt(String value) throws Exception {
        byte[] iv = new byte[12];
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(
                Cipher.ENCRYPT_MODE,
                secretKey,
                new GCMParameterSpec(128, iv)
        );

        byte[] encrypted = cipher.doFinal(
                value.getBytes(StandardCharsets.UTF_8)
        );

        byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(
                encrypted, 0, result, iv.length, encrypted.length
        );

        return Base64.getEncoder().encodeToString(result);
    }
}