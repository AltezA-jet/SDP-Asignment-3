package com.example.notification.sender;

public class SmsMessageSender implements MessageSender {

    private boolean available = true;

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            System.out.println("SMS channel is unavailable.");
            return false;
        }

        System.out.println("=== SMS Notification ===");
        System.out.println("To: " + recipient);
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
        return "SMS";
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}