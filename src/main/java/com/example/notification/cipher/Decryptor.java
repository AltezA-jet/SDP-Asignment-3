package com.example.notification.cipher;

import java.util.Scanner;

public class Decryptor {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SpaceShiftCipher cipher = new SpaceShiftCipher();

        System.out.println("=== Message Decryptor ===");
        System.out.println("Paste encrypted message or type 'exit' to quit.");

        while (true) {
            System.out.print("\nEncrypted message: ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String encrypted = scanner.nextLine();

            if (encrypted.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                String decrypted = cipher.decrypt(encrypted);
                System.out.println("Decrypted message:");
                System.out.println(decrypted);
            } catch (IllegalArgumentException e) {
                System.out.println("Decryption failed: " + e.getMessage());
            }
        }

        scanner.close();
        System.out.println("Decryptor closed.");
    }
}
