package com.example.notification.sender;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PushNotificationSenderTest {

    private static PushNotificationSender createSender() {
        return new PushNotificationSender((recipient, title, body) -> { });
    }

    @Test
    void shouldSendMessageWhenAvailable() {
        PushNotificationSender sender = createSender();

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
        PushNotificationSender sender = createSender();
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
        PushNotificationSender sender = createSender();

        sender.setAvailable(false);
        sender.setAvailable(true);

        assertTrue(sender.isAvailable());
    }

    @Test
    void shouldPassMessageToDisplay() {
        List<String> shown = new ArrayList<>();
        PushNotificationSender sender = new PushNotificationSender(
                (recipient, title, body) -> shown.add(recipient + "|" + title + "|" + body)
        );

        sender.sendMessage("device-token-123", "Title", "Body");

        assertEquals(List.of("device-token-123|Title|Body"), shown);
    }

    @Test
    void shouldFailWhenDisplayThrows() {
        PushNotificationSender sender = new PushNotificationSender(
                (recipient, title, body) -> {
                    throw new IllegalStateException("no screen");
                }
        );

        assertFalse(sender.sendMessage("device-token-123", "Title", "Body"));
    }
}