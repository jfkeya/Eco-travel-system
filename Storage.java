package com.ecotravel.storage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.ecotravel.models.User;
import com.ecotravel.models.Trip;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Storage {
    private static final String USERS_FILE = "data/users.json";
    private static final String TRIPS_FILE = "data/trips.json";

    public static void ensureDataFilesExist() {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists())
                dataDir.mkdir();

            File uf = new File(USERS_FILE);
            if (!uf.exists()) {
                JSONArray arr = new JSONArray();
                try (FileWriter fw = new FileWriter(USERS_FILE)) {
                    fw.write(arr.toJSONString());
                }
            }
            File tf = new File(TRIPS_FILE);
            if (!tf.exists()) {
                JSONArray arr = new JSONArray();
                try (FileWriter fw = new FileWriter(TRIPS_FILE)) {
                    fw.write(arr.toJSONString());
                }
            }
        } catch (Exception e) {
            System.out.println("Error creating data files: " + e.getMessage());
        }
    }

    // Users
    public static List<User> loadUsers() {
        List<User> out = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try (FileReader fr = new FileReader(USERS_FILE)) {
            Object obj = parser.parse(fr);
            JSONArray arr = (JSONArray) obj;
            for (Object o : arr) {
                JSONObject jo = (JSONObject) o;
                String username = (String) jo.get("username");
                String name = (String) jo.get("name");
                String pass = (String) jo.get("passwordHash");
                out.add(new User(username, name, pass));
            }
        } catch (Exception e) {
            // ignore -> return empty
        }
        return out;
    }

    public static void saveUsers(List<User> users) {
        JSONArray arr = new JSONArray();
        for (User u : users) {
            JSONObject jo = new JSONObject();
            jo.put("username", u.getUsername());
            jo.put("name", u.getName());
            jo.put("passwordHash", u.getPasswordHash());
            arr.add(jo);
        }
        try (FileWriter fw = new FileWriter(USERS_FILE)) {
            fw.write(arr.toJSONString());
        } catch (Exception e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    // Trips
    public static List<Trip> loadTrips() {
        List<Trip> out = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try (FileReader fr = new FileReader(TRIPS_FILE)) {
            Object obj = parser.parse(fr);
            JSONArray arr = (JSONArray) obj;
            for (Object o : arr) {
                JSONObject jo = (JSONObject) o;
                String id = (String) jo.get("id");
                String username = (String) jo.get("username");
                String from = (String) jo.get("from");
                String to = (String) jo.get("to");
                String mode = (String) jo.get("transportMode");
                double dist = ((Number) jo.get("distanceKm")).doubleValue();
                out.add(new Trip(id, username, from, to, mode, dist));
            }
        } catch (Exception e) {
            // ignore
        }
        return out;
    }

    public static void saveTrips(List<Trip> trips) {
        JSONArray arr = new JSONArray();
        for (Trip t : trips) {
            JSONObject jo = new JSONObject();
            jo.put("id", t.getId());
            jo.put("username", t.getUsername());
            jo.put("from", t.getFrom());
            jo.put("to", t.getTo());
            jo.put("transportMode", t.getTransportMode());
            jo.put("distanceKm", t.getDistanceKm());
            arr.add(jo);
        }
        try (FileWriter fw = new FileWriter(TRIPS_FILE)) {
            fw.write(arr.toJSONString());
        } catch (Exception e) {
            System.out.println("Error saving trips: " + e.getMessage());
        }
    }
}
