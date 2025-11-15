package com.ecotravel.planner;

import com.ecotravel.models.Trip;
import com.ecotravel.models.User;
import com.ecotravel.storage.Storage;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class TravelPlanner {
    private Scanner sc;

    public TravelPlanner(Scanner sc) {
        this.sc = sc;
    }

    public void planTrip(User user) {
        System.out.println("\n--- Plan a Trip ---");
        System.out.print("From: ");
        String from = sc.nextLine().trim();
        System.out.print("To: ");
        String to = sc.nextLine().trim();
        System.out.print("Approx distance in km: ");
        double dist = 0;
        try {
            dist = Double.parseDouble(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid distance. Cancelling.");
            return;
        }
        System.out.println("Suggested eco modes: ");
        System.out.println("1. Carpool");
        System.out.println("2. EV rental");
        System.out.println("3. Bus/Train");
        System.out.print("Choose mode (1-3): ");
        String m = sc.nextLine().trim();
        String mode;
        switch (m) {
            case "1":
                mode = "carpool";
                break;
            case "2":
                mode = "ev";
                break;
            case "3":
                mode = "bus/train";
                break;
            default:
                System.out.println("Invalid mode.");
                return;
        }

        String id = UUID.randomUUID().toString();
        Trip t = new Trip(id, user.getUsername(), from, to, mode, dist);
        List<Trip> trips = Storage.loadTrips();
        trips.add(t);
        Storage.saveTrips(trips);
        System.out
                .println("Trip planned and saved (ID: " + id + "). Use 'Book' to confirm or leave it as a saved trip.");
    }
}
