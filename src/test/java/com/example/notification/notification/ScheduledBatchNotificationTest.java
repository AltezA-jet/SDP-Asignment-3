package com.example.notification.notification;

import com.example.notification.sender.MessageSender;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScheduledBatchNotificationTest {

    @Test
    void shouldSendToAllRecipients() {
        FakeSender sender = new FakeSender(true);
        ScheduledBatchNotification notification =
                new ScheduledBatchNotification(sender);

        boolean result = notification.sendBatch(
                List.of("user1", "user2", "user3"),
                "Announcement",
                "Important update"
        );

        assertTrue(result);
        assertEquals(3, sender.sendCount);
        assertEquals(3, notification.getSuccessfulCount());
        assertEquals(0, notification.getFailedCount());
    }

    @Test
    void shouldCountFailedAttempts() {
        FakeSender sender = new FakeSender(false);
        ScheduledBatchNotification notification =
                new ScheduledBatchNotification(sender);

        boolean result = notification.sendBatch(
                List.of("user1", "user2"),
                "Announcement",
                "Important update"
        );

        assertFalse(result);
        assertEquals(2, sender.sendCount);
        assertEquals(0, notification.getSuccessfulCount());
        assertEquals(2, notification.getFailedCount());
    }

    @Test
    void shouldReturnFalseForEmptyRecipientList() {
        FakeSender sender = new FakeSender(true);
        ScheduledBatchNotification notification =
                new ScheduledBatchNotification(sender);

        boolean result = notification.sendBatch(
                List.of(),
                "Announcement",
                "Important update"
        );

        assertFalse(result);
        assertEquals(0, sender.sendCount);
        assertEquals(0, notification.getSuccessfulCount());
        assertEquals(0, notification.getFailedCount());
    }

    @Test
    void shouldCountBlankRecipientsAsFailed() {
        FakeSender sender = new FakeSender(true);
        ScheduledBatchNotification notification =
                new ScheduledBatchNotification(sender);

        boolean result = notification.sendBatch(
                List.of("user1", " ", "user3"),
                "Announcement",
                "Important update"
        );

        assertFalse(result);
        assertEquals(2, sender.sendCount);
        assertEquals(2, notification.getSuccessfulCount());
        assertEquals(1, notification.getFailedCount());
    }

    private static class FakeSender implements MessageSender {

        private final boolean sendResult;
        private int sendCount;

        private FakeSender(boolean sendResult) {
            this.sendResult = sendResult;
        }

        @Override
        public boolean sendMessage(
                String recipient, String title, String body
        ) {
            sendCount++;
            return sendResult;
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public String getChannelName() {
            return "Fake";
        }
    }
}