package com.example.notification.notification;

import com.example.notification.sender.MessageSender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarketingNotificationTest {

    @Test
    void shouldAddPromoPrefixToTitle() {
        FakeSender sender = new FakeSender(true, true);
        MarketingNotification notification =
                new MarketingNotification(sender);

        boolean result = notification.send(
                "user123", "Special offer", "Get 20% off"
        );

        assertTrue(result);
        assertEquals("[PROMO] Special offer", sender.lastTitle);
        assertEquals("Get 20% off", sender.lastBody);
        assertEquals(1, sender.sendCount);
    }

    @Test
    void shouldReturnFalseWhenSenderIsUnavailable() {
        FakeSender sender = new FakeSender(false, true);
        MarketingNotification notification =
                new MarketingNotification(sender);

        boolean result = notification.send(
                "user123", "Special offer", "Get 20% off"
        );

        assertFalse(result);
        assertEquals(0, sender.sendCount);
    }

    @Test
    void shouldReturnFalseWhenSendingFails() {
        FakeSender sender = new FakeSender(true, false);
        MarketingNotification notification =
                new MarketingNotification(sender);

        boolean result = notification.send(
                "user123", "Special offer", "Get 20% off"
        );

        assertFalse(result);
        assertEquals(1, sender.sendCount);
    }

    private static class FakeSender implements MessageSender {

        private final boolean available;
        private final boolean sendResult;
        private int sendCount;
        private String lastTitle;
        private String lastBody;

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
            lastBody = body;
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