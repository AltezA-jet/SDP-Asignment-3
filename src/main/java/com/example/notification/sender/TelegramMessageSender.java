package com.example.notification.sender;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TelegramMessageSender implements MessageSender {

    private final HttpClient client;
    private final String botToken;

    public TelegramMessageSender() {
        this.botToken = System.getenv("TELEGRAM_BOT_TOKEN");
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            return false;
        }

        String text = escapeHtml(title + "\n\n" + body);
        String json = "{\"chat_id\":\"" + escapeJson(recipient)
                + "\",\"text\":\"" + escapeJson(text)
                + "\",\"parse_mode\":\"HTML\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.telegram.org/bot"
                        + botToken + "/sendMessage"))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, java.nio.charset.StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200
                    && response.body().contains("\"ok\":true")) {
                return true;
            }

            System.err.println("Telegram API error: "
                    + response.statusCode() + " " + response.body());
            return false;

        } catch (IOException e) {
            System.err.println("Telegram request failed: " + e.getMessage());
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean isAvailable() {
        return botToken != null && !botToken.isBlank();
    }

    @Override
    public String getChannelName() {
        return "Telegram";
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
