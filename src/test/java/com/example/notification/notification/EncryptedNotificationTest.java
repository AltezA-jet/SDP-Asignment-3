package com.example.notification.notification;

import com.example.notification.sender.MessageSender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptedNotificationTest {

    @Test
    void shouldEncryptTitleAndBodyBeforeSending() {
        FakeSender sender = new FakeSender();
        byte[] key = new byte[32];

        EncryptedNotification notification =
                new EncryptedNotification(sender, key);

        boolean result = notification.send(
                "user123",
                "Private title",
                "Confidential message"
        );

        assertTrue(result);
        assertNotEquals("Private title", sender.lastTitle);
        assertNotEquals("Confidential message", sender.lastBody);
        assertNotNull(sender.lastTitle);
        assertNotNull(sender.lastBody);
    }

    @Test
    void shouldReturnFalseWhenSenderIsUnavailable() {
        FakeSender sender = new FakeSender();
        sender.available = false;

        EncryptedNotification notification =
                new EncryptedNotification(sender, new byte[32]);

        boolean result = notification.send(
                "user123",
                "Private title",
                "Confidential message"
        );

        assertFalse(result);
        assertEquals(0, sender.sendCount);
    }

    @Test
    void shouldRejectInvalidKeyLength() {
        FakeSender sender = new FakeSender();

        assertThrows(
                IllegalArgumentException.class,
                () -> new EncryptedNotification(sender, new byte[16])
        );
    }

    private static class FakeSender implements MessageSender {

        private boolean available = true;
        private int sendCount;
        private String lastTitle;
        private String lastBody;

        @Override
        public boolean sendMessage(
                String recipient,
                String title,
                String body
        ) {
            sendCount++;
            lastTitle = title;
            lastBody = body;
            return true;
        }

        @Override
        public boolean isAvailable() {
            return available;
        }

        @Override
        public String getChannelName() {
            return "Fake";
        }
    }
}