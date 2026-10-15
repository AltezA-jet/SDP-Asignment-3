package com.example.notification.sender;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SmsMessageSenderTest {

    @TempDir
    Path directory;

    @Test
    void shouldSendMessageWhenAvailable() {
        SmsMessageSender sender = new SmsMessageSender(directory);

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
        SmsMessageSender sender = new SmsMessageSender(directory);
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
        SmsMessageSender sender = new SmsMessageSender(directory);

        sender.setAvailable(false);
        sender.setAvailable(true);

        assertTrue(sender.isAvailable());
    }

    @Test
    void shouldSaveMessagesToFileNamedByRecipient() throws IOException {
        SmsMessageSender sender = new SmsMessageSender(directory);

        sender.sendMessage("+77001234567", "First", "Hello");
        sender.sendMessage("+77001234567", "Second", "World");

        String content = Files.readString(
                directory.resolve("+77001234567.txt"),
                StandardCharsets.UTF_8
        );

        assertTrue(content.contains("Title: First"));
        assertTrue(content.contains("Message: Hello"));
        assertTrue(content.contains("Title: Second"));
        assertTrue(content.contains("Message: World"));
    }

    @Test
    void shouldKeepFileInsideDirectoryForUnsafeRecipient() throws IOException {
        SmsMessageSender sender = new SmsMessageSender(directory);

        boolean result = sender.sendMessage("..\\evil/name", "Title", "Body");

        assertTrue(result);
        assertTrue(Files.exists(directory.resolve(".._evil_name.txt")));
        assertFalse(Files.exists(directory.getParent().resolve("evil")));
    }

    @Test
    void shouldFailForEmptyRecipient() {
        SmsMessageSender sender = new SmsMessageSender(directory);

        assertFalse(sender.sendMessage("   ", "Title", "Body"));
        assertFalse(sender.sendMessage(null, "Title", "Body"));
    }

    @Test
    void shouldAcceptMessageOfExactlyMaxLength() {
        SmsMessageSender sender = new SmsMessageSender(directory);

        // заголовок 10 + текст 150 = 160 символов
        boolean result = sender.sendMessage(
                "+77001234567",
                "a".repeat(10),
                "b".repeat(150)
        );

        assertTrue(result);
    }

    @Test
    void shouldRejectMessageLongerThanMaxLength() {
        SmsMessageSender sender = new SmsMessageSender(directory);

        // заголовок 10 + текст 151 = 161 символ
        boolean result = sender.sendMessage(
                "+77001234567",
                "a".repeat(10),
                "b".repeat(151)
        );

        assertFalse(result);
        assertFalse(Files.exists(directory.resolve("+77001234567.txt")));
    }

    @Test
    void shouldUseConfiguredMaxLength() {
        SmsMessageSender sender = new SmsMessageSender(directory, 120);

        assertTrue(sender.sendMessage("+77001234567", "a", "b".repeat(119)));
        assertFalse(sender.sendMessage("+77001234567", "a", "b".repeat(120)));
    }
}