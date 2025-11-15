package com.ecotravel.booking;

import com.ecotravel.models.Trip;
import com.ecotravel.models.User;
import com.ecotravel.storage.Storage;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class BookingManager {
    private Scanner sc;

    public BookingManager(Scanner sc) {
        this.sc = sc;
    }

    public void showOptionsAndBook(User user) {
        System.out.println("\n--- Eco Options & Booking ---");
        System.out.println("1. Quick EV rental (estimate)");
        System.out.println("2. Join carpool (simulated)");
        System.out.println("3. Search saved planned trips to book");
        System.out.print("Choose: ");
        String c = sc.nextLine().trim();
        switch (c) {
            case "1":
                quickBook(user, "ev");
                break;
            case "2":
                quickBook(user, "carpool");
                break;
            case "3":
                searchAndBook(user);
                break;
            default:
                System.out.println("Invalid.");
        }
    }

    private void quickBook(User user, String mode) {
        System.out.print("From: ");
        String from = sc.nextLine().trim();
        System.out.print("To: ");
        String to = sc.nextLine().trim();
        System.out.print("Distance km (approx): ");
        double dist;
        try {
            dist = Double.parseDouble(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid distance.");
            return;
        }
        String id = UUID.randomUUID().toString();
        Trip t = new Trip(id, user.getUsername(), from, to, mode, dist);
        List<Trip> trips = Storage.loadTrips();
        trips.add(t);
        Storage.saveTrips(trips);
        System.out.println("Booked " + mode + " trip. Booking ID: " + id);
    }

    private void searchAndBook(User user) {
        List<Trip> trips = Storage.loadTrips();
        System.out.println("Your saved trips:");
        int i = 0;
        for (Trip t : trips) {
            if (t.getUsername().equals(user.getUsername())) {
                i++;
                System.out.printf("%d) ID:%s | %s -> %s | %s | %.2f km\n", i, t.getId(), t.getFrom(), t.getTo(),
                        t.getTransportMode(), t.getDistanceKm());
            }
        }
        if (i == 0) {
            System.out.println("No saved trips.");
            return;
        }
        System.out.print("Enter booking ID to confirm (type the ID): ");
        String id = sc.nextLine().trim();
        boolean found = false;
        for (Trip t : trips) {
            if (t.getId().equals(id) && t.getUsername().equals(user.getUsername())) {
                System.out.println("Booking confirmed for trip ID " + id);
                found = true;
                break;
            }
        }
        if (!found)
            System.out.println("Trip ID not found.");
    }

    public void viewBookings(User user) {
        List<Trip> trips = Storage.loadTrips();
        System.out.println("\n--- My Bookings & Planned Trips ---");
        int i = 0;
        for (Trip t : trips) {
            if (t.getUsername().equals(user.getUsername())) {
                i++;
                System.out.printf("%d) ID:%s | %s -> %s | %s | %.2f km\n", i, t.getId(), t.getFrom(), t.getTo(),
                        t.getTransportMode(), t.getDistanceKm());
            }
        }
        if (i == 0)
            System.out.println("No bookings or planned trips found.");
    }
}
