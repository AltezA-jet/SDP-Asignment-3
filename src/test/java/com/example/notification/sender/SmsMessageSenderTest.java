package com.example.notification.sender;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SmsMessageSenderTest {

    @Test
    void shouldSendMessageWhenAvailable() {
        SmsMessageSender sender = new SmsMessageSender();

        boolean result = sender.sendMessage(
                "+77001234567",
                "Test title",
                "Test message"
        );

        assertTrue(result);
        assertTrue(sender.isAvailable());
        assertEquals("SMS", sender.getChannelName());
    }

    @Test
    void shouldFailWhenUnavailable() {
        SmsMessageSender sender = new SmsMessageSender();
        sender.setAvailable(false);

        boolean result = sender.sendMessage(
                "+77001234567",
                "Test title",
                "Test message"
        );

        assertFalse(result);
        assertFalse(sender.isAvailable());
    }

    @Test
    void shouldRestoreAvailability() {
        SmsMessageSender sender = new SmsMessageSender();

        sender.setAvailable(false);
        sender.setAvailable(true);

        assertTrue(sender.isAvailable());
    }
}