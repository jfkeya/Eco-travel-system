package com.ecotravel.auth;

import com.ecotravel.models.User;
import com.ecotravel.storage.Storage;

import java.security.MessageDigest;
import java.util.List;
import java.util.Scanner;

public class AuthManager {
    private Scanner sc;

    public AuthManager(Scanner sc) {
        this.sc = sc;
    }

    public void register() {
        System.out.println("\n--- Register ---");
        System.out.print("Username: ");
        String username = sc.nextLine().trim();
        System.out.print("Full name: ");
        String name = sc.nextLine().trim();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        List<User> users = Storage.loadUsers();
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                System.out.println("Username already exists.");
                return;
            }
        }

        String hash = hash(pass);
        users.add(new User(username, name, hash));
        Storage.saveUsers(users);
        System.out.println("Registered successfully.");
    }

    public User login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = sc.nextLine().trim();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        List<User> users = Storage.loadUsers();
        String hash = hash(pass);
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username) && u.getPasswordHash().equals(hash)) {
                System.out.println("Login successful.");
                return u;
            }
        }
        System.out.println("Invalid credentials.");
        return null;
    }

    private String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte x : b) {
                sb.append(String.format("%02x", x));
            }
            return sb.toString();
        } catch (Exception e) {
            return input; 
        }
    }
}
