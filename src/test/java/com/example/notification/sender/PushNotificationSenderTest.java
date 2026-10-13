package com.example.notification.sender;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PushNotificationSenderTest {

    @Test
    void shouldSendMessageWhenAvailable() {
        PushNotificationSender sender = new PushNotificationSender();

        boolean result = sender.sendMessage(
                "device-token-123",
                "Test title",
                "Test message"
        );

        assertTrue(result);
        assertTrue(sender.isAvailable());
        assertEquals("Push Notification", sender.getChannelName());
    }

    @Test
    void shouldFailWhenUnavailable() {
        PushNotificationSender sender = new PushNotificationSender();
        sender.setAvailable(false);

        boolean result = sender.sendMessage(
                "device-token-123",
                "Test title",
                "Test message"
        );

        assertFalse(result);
        assertFalse(sender.isAvailable());
    }

    @Test
    void shouldRestoreAvailability() {
        PushNotificationSender sender = new PushNotificationSender();

        sender.setAvailable(false);
        sender.setAvailable(true);

        assertTrue(sender.isAvailable());
    }
}