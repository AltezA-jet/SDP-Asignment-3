package com.example.notification.sender;

public class PushNotificationSender implements MessageSender {

    private boolean available = true;

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            System.out.println("Push notification channel is unavailable.");
            return false;
        }

        System.out.println("=== Push Notification ===");
        System.out.println("Device token: " + recipient);
        System.out.println("Title: " + title);
        System.out.println("Message: " + body);
        System.out.println("=========================");

        return true;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public String getChannelName() {
        return "Push Notification";
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}