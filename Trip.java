package com.ecotravel.models;

public class Trip {
    private String id;
    private String username;
    private String from;
    private String to;
    private String transportMode; 
    private double distanceKm;

    public Trip(String id, String username, String from, String to, String transportMode, double distanceKm) {
        this.id = id;
        this.username = username;
        this.from = from;
        this.to = to;
        this.transportMode = transportMode;
        this.distanceKm = distanceKm;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public double getDistanceKm() {
        return distanceKm;
    }
}
