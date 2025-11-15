package com.ecotravel;

import com.ecotravel.auth.AuthManager;
import com.ecotravel.booking.BookingManager;
import com.ecotravel.planner.TravelPlanner;
import com.ecotravel.tracker.CarbonTracker;
import com.ecotravel.models.User;
import com.ecotravel.storage.Storage;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Storage.ensureDataFilesExist();
        Scanner sc = new Scanner(System.in);
        AuthManager auth = new AuthManager(sc);
        TravelPlanner planner = new TravelPlanner(sc);
        BookingManager bookingManager = new BookingManager(sc);
        CarbonTracker tracker = new CarbonTracker();

        while (true) {
            System.out.println("\n=== Eco Travel System ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    auth.register();
                    break;
                case "2":
                    User user = auth.login();
                    if (user != null) {
                        userMenu(user, sc, planner, bookingManager, tracker);
                    }
                    break;
                case "3":
                    System.out.println("Goodbye! Keep traveling green 🌿");
                    sc.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void userMenu(User user, Scanner sc, TravelPlanner planner, BookingManager bookingManager, CarbonTracker tracker) {
        System.out.println("\nWelcome, " + user.getName());
        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Plan a Trip");
            System.out.println("2. View & Book Eco Options");
            System.out.println("3. My Bookings");
            System.out.println("4. Calculate Carbon Footprint for a Trip");
            System.out.println("5. Logout");
            System.out.print("Choose: ");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1":
                    planner.planTrip(user);
                    break;
                case "2":
                    bookingManager.showOptionsAndBook(user);
                    break;
                case "3":
                    bookingManager.viewBookings(user);
                    break;
                case "4":
                    tracker.interactiveCalculate(sc);
                    break;
                case "5":
                    running = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
