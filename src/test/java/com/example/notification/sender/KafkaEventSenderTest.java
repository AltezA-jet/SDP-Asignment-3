package com.example.notification.sender;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KafkaEventSenderTest {

    @Test
    void shouldPublishEventWhenAvailable() {
        KafkaEventSender sender = new KafkaEventSender();

        boolean result = sender.sendMessage(
                "notifications",
                "UserRegistered",
                "User 123 registered"
        );

        assertTrue(result);
        assertTrue(sender.isAvailable());
        assertEquals("Kafka", sender.getChannelName());
    }

    @Test
    void shouldFailWhenUnavailable() {
        KafkaEventSender sender = new KafkaEventSender();
        sender.setAvailable(false);

        boolean result = sender.sendMessage(
                "notifications",
                "UserRegistered",
                "User 123 registered"
        );

        assertFalse(result);
        assertFalse(sender.isAvailable());
    }

    @Test
    void shouldRestoreAvailability() {
        KafkaEventSender sender = new KafkaEventSender();

        sender.setAvailable(false);
        sender.setAvailable(true);

        assertTrue(sender.isAvailable());
    }
}