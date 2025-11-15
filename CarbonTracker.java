package com.ecotravel.tracker;

import java.util.Scanner;

/**
 * Simple carbon footprint calculator. These are simplified emission factors (kg
 * CO2e per km).
 * - carpool: 0.12 (shared)
 * - ev: 0.05 (grid dependent, simplified)
 * - bus/train: 0.04
 * - private car: 0.21
 *
 * These are example values to let the system suggest greener options.
 */
public class CarbonTracker {
    public void interactiveCalculate(Scanner sc) {
        System.out.println("\n--- Carbon Footprint Calculator ---");
        System.out.print("Distance (km): ");
        double d;
        try {
            d = Double.parseDouble(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Invalid distance.");
            return;
        }
        System.out.println("Choose transport mode:");
        System.out.println("1. Private car");
        System.out.println("2. Carpool");
        System.out.println("3. EV rental");
        System.out.println("4. Bus/Train");
        System.out.print("Choose: ");
        String ch = sc.nextLine().trim();
        String mode;
        switch (ch) {
            case "1":
                mode = "private_car";
                break;
            case "2":
                mode = "carpool";
                break;
            case "3":
                mode = "ev";
                break;
            case "4":
                mode = "bus_train";
                break;
            default:
                System.out.println("Invalid.");
                return;
        }
        double kg = calculateCarbon(d, mode);
        System.out.printf("Estimated emissions for %.2f km using %s : %.2f kg CO2e\n", d, mode, kg);
        suggestAlternative(mode, kg);
    }

    public double calculateCarbon(double km, String mode) {
        double factor;
        switch (mode) {
            case "carpool":
                factor = 0.12;
                break;
            case "ev":
                factor = 0.05;
                break;
            case "bus_train":
                factor = 0.04;
                break;
            case "private_car":
            default:
                factor = 0.21;
                break;
        }
        return km * factor;
    }

    private void suggestAlternative(String mode, double kg) {
        System.out.println("\nSuggestions to reduce carbon:");
        if (!mode.equals("bus_train")) {
            System.out.println("- Consider taking bus/train when available (lower emissions).");
        }
        if (!mode.equals("carpool")) {
            System.out.println("- Try carpooling with others to split emissions.");
        }
        if (!mode.equals("ev")) {
            System.out.println("- Use EV rentals where charging is from renewables.");
        }
        System.out.println(
                String.format("- Estimated footprint: %.2f kg CO2e. Try shorter trips or combine errands!", kg));
    }
}
