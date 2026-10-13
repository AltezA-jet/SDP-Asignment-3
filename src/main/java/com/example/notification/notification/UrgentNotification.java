package com.example.notification.notification;

import com.example.notification.sender.MessageSender;

public class UrgentNotification extends Notification {

    private final MessageSender fallbackSender;

    public UrgentNotification(
            MessageSender sender,
            MessageSender fallbackSender
    ) {
        super(sender);

        if (fallbackSender == null) {
            throw new IllegalArgumentException(
                    "Fallback sender cannot be null"
            );
        }

        this.fallbackSender = fallbackSender;
    }

    @Override
    public boolean send(String recipient, String title, String body) {
        String urgentTitle = "[URGENT] " + title;

        if (sender.isAvailable()
                && sender.sendMessage(recipient, urgentTitle, body)) {
            return true;
        }

        System.out.println("Primary channel failed. Trying fallback channel: "
                + fallbackSender.getChannelName());

        return fallbackSender.isAvailable()
                && fallbackSender.sendMessage(
                        recipient,
                        urgentTitle,
                        body
                );
    }
}