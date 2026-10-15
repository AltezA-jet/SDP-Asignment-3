package com.example.notification.sender;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Set;

public class SmsMessageSender implements MessageSender {

    private static final Path DEFAULT_DIRECTORY = Path.of("sms");
    private static final int DEFAULT_MAX_LENGTH = 160;

    private static final Set<String> RESERVED_NAMES = Set.of(
            "CON", "PRN", "AUX", "NUL",
            "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9",
            "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"
    );

    private final Path directory;
    private final int maxLength;
    private boolean available = true;

    public SmsMessageSender() {
        this(DEFAULT_DIRECTORY, DEFAULT_MAX_LENGTH);
    }

    public SmsMessageSender(Path directory) {
        this(directory, DEFAULT_MAX_LENGTH);
    }

    public SmsMessageSender(Path directory, int maxLength) {
        if (directory == null) {
            throw new IllegalArgumentException("Directory cannot be null");
        }

        if (maxLength <= 0) {
            throw new IllegalArgumentException("Max length must be positive");
        }

        this.directory = directory;
        this.maxLength = maxLength;
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            System.out.println("SMS channel is unavailable.");
            return false;
        }

        int length = lengthOf(title) + lengthOf(body);

        if (length > maxLength) {
            System.out.println("SMS is too long: " + length
                    + " characters, maximum is " + maxLength + ".");
            return false;
        }

        String fileName = toFileName(recipient);

        if (fileName == null) {
            System.out.println("SMS recipient is empty.");
            return false;
        }

        String separator = System.lineSeparator();
        String entry = "Title: " + title + separator
                + "Message: " + body + separator
                + "---" + separator;

        try {
            Files.createDirectories(directory);
            Files.writeString(
                    directory.resolve(fileName),
                    entry,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            return true;
        } catch (IOException | InvalidPathException e) {
            System.err.println("Failed to save SMS: " + e.getMessage());
            return false;
        }
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

    /** Длина текста в символах (null считается пустой строкой). */
    private static int lengthOf(String text) {
        return text == null ? 0 : text.codePointCount(0, text.length());
    }

    /** Имя файла по получателю: недопустимые символы заменяются, пустое имя недопустимо. */
    private static String toFileName(String recipient) {
        if (recipient == null) {
            return null;
        }

        String name = recipient.trim()
                .replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "_");

        if (name.isEmpty()) {
            return null;
        }

        String base = name.split("\\.", 2)[0].toUpperCase(Locale.ROOT);

        if (RESERVED_NAMES.contains(base)) {
            name = "_" + name;
        }

        return name + ".txt";
    }
}