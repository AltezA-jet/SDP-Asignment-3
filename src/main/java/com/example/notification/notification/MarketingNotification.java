package com.example.notification.notification;

import com.example.notification.sender.MessageSender;

public class MarketingNotification extends Notification {

    public MarketingNotification(MessageSender sender) {
        super(sender);
    }

    @Override
    public boolean send(String recipient, String title, String body) {
        if (!sender.isAvailable()) {
            System.out.println("Marketing notification channel is unavailable.");
            return false;
        }

        String marketingTitle = "[PROMO] " + title;

        return sender.sendMessage(recipient, marketingTitle, body);
    }
}