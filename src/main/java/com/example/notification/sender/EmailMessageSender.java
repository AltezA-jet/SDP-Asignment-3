
package com.example.notification.sender;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailMessageSender implements MessageSender {

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            System.out.println("Email channel is unavailable. Check SMTP configuration.");
            return false;
        }

        try {
            String host = System.getenv("SMTP_HOST");
            String port = System.getenv("SMTP_PORT");
            String username = System.getenv("SMTP_USERNAME");
            String password = System.getenv("SMTP_PASSWORD");
            String from = System.getenv("SMTP_FROM");

            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", port);

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(recipient)
            );
            message.setSubject(title);
            message.setText(body);

            Transport.send(message);

            System.out.println("Email sent successfully to " + recipient);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isAvailable() {
        return hasValue("SMTP_HOST")
                && hasValue("SMTP_PORT")
                && hasValue("SMTP_USERNAME")
                && hasValue("SMTP_PASSWORD")
                && hasValue("SMTP_FROM");
    }

    @Override
    public String getChannelName() {
        return "Email";
    }

    private boolean hasValue(String name) {
        String value = System.getenv(name);
        return value != null && !value.isBlank();
    }
}
