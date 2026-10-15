package com.example.notification.sender;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class PushNotificationSender implements MessageSender {

    /** Способ показать push-уведомление пользователю. */
    @FunctionalInterface
    public interface PopupDisplay {
        void show(String recipient, String title, String body);
    }

    private final PopupDisplay display;
    private boolean available = true;

    public PushNotificationSender() {
        this(PushNotificationSender::showSwingPopup);
    }

    public PushNotificationSender(PopupDisplay display) {
        if (display == null) {
            throw new IllegalArgumentException("Display cannot be null");
        }

        this.display = display;
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        if (!isAvailable()) {
            System.out.println("Push notification channel is unavailable.");
            return false;
        }

        try {
            display.show(recipient, title, body);
            return true;
        } catch (RuntimeException e) {
            System.out.println("Failed to show push notification: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public String getChannelName() {
        return "Push Notification";
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    private static void showSwingPopup(String recipient, String title, String body) {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException("Push popup needs a graphical environment");
        }

        SwingUtilities.invokeLater(() -> {
            JOptionPane pane = new JOptionPane(
                    "Device token: " + recipient + "\n\n" + body,
                    JOptionPane.INFORMATION_MESSAGE
            );
            JDialog dialog = pane.createDialog(title);
            dialog.setModal(false);
            dialog.setAlwaysOnTop(true);
            dialog.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentHidden(ComponentEvent e) {
                    dialog.dispose();
                }
            });
            dialog.setVisible(true);
        });
    }
}