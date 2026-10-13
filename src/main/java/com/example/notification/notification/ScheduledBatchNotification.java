package com.example.notification.notification;

import com.example.notification.sender.MessageSender;

import java.util.List;

public class ScheduledBatchNotification extends Notification {

    private int successfulCount;
    private int failedCount;

    public ScheduledBatchNotification(MessageSender sender) {
        super(sender);
    }

    @Override
    public boolean send(String recipient, String title, String body) {
        return sendBatch(List.of(recipient), title, body);
    }

    public boolean sendBatch(
            List<String> recipients,
            String title,
            String body
    ) {
        if (recipients == null || recipients.isEmpty()) {
            successfulCount = 0;
            failedCount = 0;
            return false;
        }

        successfulCount = 0;
        failedCount = 0;

        for (String recipient : recipients) {
            if (recipient == null || recipient.isBlank()) {
                failedCount++;
                continue;
            }

            if (sender.isAvailable()
                    && sender.sendMessage(recipient, title, body)) {
                successfulCount++;
            } else {
                failedCount++;
            }
        }

        System.out.println("=== Batch Summary ===");
        System.out.println("Successful: " + successfulCount);
        System.out.println("Failed: " + failedCount);
        System.out.println("=====================");

        return failedCount == 0;
    }

    public int getSuccessfulCount() {
        return successfulCount;
    }

    public int getFailedCount() {
        return failedCount;
    }
}