package com.example.notification.notification;

import com.example.notification.sender.MessageSender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrgentNotificationTest {

    @Test
    void shouldSendThroughPrimaryChannel() {
        FakeSender primary = new FakeSender(true, true);
        FakeSender fallback = new FakeSender(true, true);

        UrgentNotification notification =
                new UrgentNotification(primary, fallback);

        boolean result = notification.send(
                "user123", "Server alert", "Server is down"
        );

        assertTrue(result);
        assertEquals(1, primary.sendCount);
        assertEquals(0, fallback.sendCount);
        assertEquals("[URGENT] Server alert", primary.lastTitle);
    }

    @Test
    void shouldUseFallbackWhenPrimaryIsUnavailable() {
        FakeSender primary = new FakeSender(false, true);
        FakeSender fallback = new FakeSender(true, true);

        UrgentNotification notification =
                new UrgentNotification(primary, fallback);

        boolean result = notification.send(
                "user123", "Server alert", "Server is down"
        );

        assertTrue(result);
        assertEquals(0, primary.sendCount);
        assertEquals(1, fallback.sendCount);
    }

    @Test
    void shouldUseFallbackWhenPrimarySendFails() {
        FakeSender primary = new FakeSender(true, false);
        FakeSender fallback = new FakeSender(true, true);

        UrgentNotification notification =
                new UrgentNotification(primary, fallback);

        boolean result = notification.send(
                "user123", "Server alert", "Server is down"
        );

        assertTrue(result);
        assertEquals(1, primary.sendCount);
        assertEquals(1, fallback.sendCount);
    }

    @Test
    void shouldReturnFalseWhenBothChannelsFail() {
        FakeSender primary = new FakeSender(false, false);
        FakeSender fallback = new FakeSender(false, false);

        UrgentNotification notification =
                new UrgentNotification(primary, fallback);

        boolean result = notification.send(
                "user123", "Server alert", "Server is down"
        );

        assertFalse(result);
        assertEquals(0, primary.sendCount);
        assertEquals(0, fallback.sendCount);
    }

    private static class FakeSender implements MessageSender {

        private final boolean available;
        private final boolean sendResult;
        private int sendCount;
        private String lastTitle;

        private FakeSender(boolean available, boolean sendResult) {
            this.available = available;
            this.sendResult = sendResult;
        }

        @Override
        public boolean sendMessage(
                String recipient, String title, String body
        ) {
            sendCount++;
            lastTitle = title;
            return sendResult;
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