package com.example.notification.notification;

import com.example.notification.cipher.SpaceShiftCipher;
import com.example.notification.sender.MessageSender;

public class SpaceShiftEncryptedNotification extends Notification {

    private final SpaceShiftCipher cipher = new SpaceShiftCipher();

    public SpaceShiftEncryptedNotification(MessageSender sender) {
        super(sender);
    }

    @Override
    public boolean send(String recipient, String title, String body) {
        try {
            String encryptedTitle = cipher.encrypt(title);
            String encryptedBody = cipher.encrypt(body);

            return sender.isAvailable()
                    && sender.sendMessage(
                            recipient,
                            encryptedTitle,
                            encryptedBody
                    );
        } catch (IllegalArgumentException e) {
            System.err.println(
                    "Failed to encrypt notification: " + e.getMessage()
            );
            return false;
        }
    }
}