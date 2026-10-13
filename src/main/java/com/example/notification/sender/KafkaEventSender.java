package com.example.notification.sender;

public class KafkaEventSender implements MessageSender {

    private boolean available = true;

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            System.out.println("Kafka channel is unavailable.");
            return false;
        }

        System.out.println("=== Kafka Event ===");
        System.out.println("Topic: " + recipient);
        System.out.println("Event: " + title);
        System.out.println("Payload: " + body);
        System.out.println("===================");

        return true;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public String getChannelName() {
        return "Kafka";
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}