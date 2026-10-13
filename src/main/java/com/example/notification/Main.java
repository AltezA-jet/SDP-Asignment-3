
package com.example.notification;

import com.example.notification.sender.*;
import com.example.notification.notification.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        MessageSender sms = new SmsMessageSender();
        MessageSender push = new PushNotificationSender();
        MessageSender kafka = new KafkaEventSender();
        MessageSender telegram = new TelegramMessageSender();
        MessageSender email = new EmailMessageSender();

        while (true) {
            System.out.println("\n=== Notification Gateway ===");
            System.out.println("1. SMS");
            System.out.println("2. Push Notification");
            System.out.println("3. Kafka Event");
            System.out.println("4. Telegram");
            System.out.println("5. Email");
            System.out.println("0. Exit");
            System.out.print("Choose channel: ");

            String choice = scanner.nextLine();

            if (choice.equals("0")) {
                break;
            }

            MessageSender sender = switch (choice) {
                case "1" -> sms;
                case "2" -> push;
                case "3" -> kafka;
                case "4" -> telegram;
                case "5" -> email;
                default -> null;
            };

            if (sender == null) {
                System.out.println("Invalid choice.");
                continue;
            }

            System.out.println("Channel: " + sender.getChannelName());
            System.out.println("Available: " + sender.isAvailable());

            if (!sender.isAvailable()) {
                System.out.println("Configure the required environment variables first.");
                continue;
            }

            System.out.print("Recipient: ");
            String recipient = scanner.nextLine();

            System.out.print("Title: ");
            String title = scanner.nextLine();

            System.out.print("Message: ");
            String body = scanner.nextLine();

            System.out.println("\nNotification type:");
            System.out.println("1. Regular");
            System.out.println("2. Urgent");
            System.out.println("3. Marketing");
            System.out.println("4. Encrypted");
            System.out.print("Choose type: ");

            String type = scanner.nextLine();

            Notification notification;

            switch (type) {
                case "1" -> notification = new Notification(sender) {
                    @Override
                    public boolean send(String recipient, String title, String body) {
                        if (!sender.isAvailable()) {
                            return false;
                        }
                        return sender.sendMessage(recipient, title, body);
                    }
                };
                case "2" -> notification = new UrgentNotification(sender, sms);
                case "3" -> notification = new MarketingNotification(sender);
                case "4" -> {
                    System.out.println(
                            "Encrypted notification requires a valid 32-byte AES key."
                    );
                    System.out.println(
                            "Use the EncryptedNotification class with a configured key."
                    );
                    continue;
                }
                default -> {
                    System.out.println("Invalid notification type.");
                    continue;
                }
            }

            boolean success = notification.send(recipient, title, body);
            System.out.println(
                    success ? "Notification sent successfully."
                            : "Notification sending failed."
            );
        }

        scanner.close();
        System.out.println("Application closed.");
    }
}
