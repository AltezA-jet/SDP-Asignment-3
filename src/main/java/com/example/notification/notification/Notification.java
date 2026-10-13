package com.example.notification.notification;

import com.example.notification.sender.MessageSender;

public abstract class Notification {

    protected MessageSender sender;

    public Notification(MessageSender sender) {
        if (sender == null) {
            throw new IllegalArgumentException("MessageSender cannot be null");
        }

        this.sender = sender;
    }

    public void setSender(MessageSender sender) {
        if (sender == null) {
            throw new IllegalArgumentException("MessageSender cannot be null");
        }

        this.sender = sender;
    }

    public MessageSender getSender() {
        return sender;
    }

    public abstract boolean send(
            String recipient,
            String title,
            String body
    );
}