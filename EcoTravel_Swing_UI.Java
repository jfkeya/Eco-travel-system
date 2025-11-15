package com.ecotravel.ui;

import com.ecotravel.auth.AuthManager;
import com.ecotravel.booking.BookingManager;
import com.ecotravel.models.Trip;
import com.ecotravel.models.User;
import com.ecotravel.planner.TravelPlanner;
import com.ecotravel.storage.Storage;
import com.ecotravel.tracker.CarbonTracker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;


public class EcoTravel_Swing_UI {
    private JFrame frame;
    private CardLayout cards;
    private JPanel cardPanel;

    private AuthManager authManager;
    private TravelPlanner planner;
    private BookingManager bookingManager;
    private CarbonTracker tracker;

    private User currentUser;

    private JLabel lblWelcome;
    private JTable bookingsTable;
    private DefaultTableModel bookingsModel;

    public EcoTravel_Swing_UI() {
        Storage.ensureDataFilesExist();
        authManager = new AuthManager(new java.util.Scanner(System.in));
        planner = new TravelPlanner(new java.util.Scanner(System.in));
        bookingManager = new BookingManager(new java.util.Scanner(System.in));
        tracker = new CarbonTracker();

        initUI();
    }

    private void initUI() {
        frame = new JFrame("Eco Travel - Swing UI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        cards = new CardLayout();
        cardPanel = new JPanel(cards);

        cardPanel.add(loginPanel(), "login");
        cardPanel.add(registerPanel(), "register");
        cardPanel.add(dashboardPanel(), "dashboard");
        cardPanel.add(planTripPanel(), "plan");
        cardPanel.add(bookPanel(), "book");
        cardPanel.add(carbonPanel(), "carbon");

        frame.getContentPane().add(cardPanel);
        frame.setVisible(true);

        cards.show(cardPanel, "login");
    }

    private JPanel loginPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Eco Travel", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        p.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.EAST;
        center.add(new JLabel("Username:"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        JTextField tfUser = new JTextField(20);
        center.add(tfUser, c);

        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.EAST;
        center.add(new JLabel("Password:"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        JPasswordField pf = new JPasswordField(20);
        center.add(pf, c);

        p.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel();
        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("Register");
        south.add(btnLogin);
        south.add(btnRegister);
        p.add(south, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> {
            String username = tfUser.getText().trim();
            String pass = new String(pf.getPassword());
            User u = attemptLogin(username, pass);
            if (u != null) {
                currentUser = u;
                lblWelcome.setText("Welcome, " + currentUser.getName());
                refreshBookingsTable();
                cards.show(cardPanel, "dashboard");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnRegister.addActionListener(e -> cards.show(cardPanel, "register"));

        return p;
    }

    private JPanel registerPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Register New Account", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        p.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.EAST;
        form.add(new JLabel("Username:"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        JTextField tfUser = new JTextField(20);
        form.add(tfUser, c);

        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.EAST;
        form.add(new JLabel("Full name:"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        JTextField tfName = new JTextField(20);
        form.add(tfName, c);

        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.EAST;
        form.add(new JLabel("Password:"), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.WEST;
        JPasswordField pf = new JPasswordField(20);
        form.add(pf, c);

        p.add(form, BorderLayout.CENTER);

        JPanel south = new JPanel();
        JButton btnCreate = new JButton("Create");
        JButton btnBack = new JButton("Back");
        south.add(btnCreate);
        south.add(btnBack);
        p.add(south, BorderLayout.SOUTH);

        btnCreate.addActionListener(e -> {
            String username = tfUser.getText().trim();
            String name = tfName.getText().trim();
            String pass = new String(pf.getPassword());
            if (username.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            List<User> users = Storage.loadUsers();
            for (User u : users) {
                if (u.getUsername().equalsIgnoreCase(username)) {
                    JOptionPane.showMessageDialog(frame, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            // compute SHA256
            String hash = sha256(pass);
            users.add(new User(username, name, hash));
            Storage.saveUsers(users);
            JOptionPane.showMessageDialog(frame, "Registration successful. You can login now.", "OK",
                    JOptionPane.INFORMATION_MESSAGE);
            cards.show(cardPanel, "login");
        });

        btnBack.addActionListener(e -> cards.show(cardPanel, "login"));

        return p;
    }

    private JPanel dashboardPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new BorderLayout());
        lblWelcome = new JLabel("Welcome, Guest");
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 18));
        top.add(lblWelcome, BorderLayout.WEST);
        JButton btnLogout = new JButton("Logout");
        top.add(btnLogout, BorderLayout.EAST);
        p.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));

        // Left: actions
        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setBorder(new EmptyBorder(10, 10, 10, 10));
        JButton btnPlan = new JButton("Plan a Trip");
        JButton btnBook = new JButton("Book Options");
        JButton btnMyBookings = new JButton("My Bookings");
        JButton btnCarbon = new JButton("Carbon Calculator");
        actions.add(btnPlan);
        actions.add(Box.createRigidArea(new Dimension(0, 8)));
        actions.add(btnBook);
        actions.add(Box.createRigidArea(new Dimension(0, 8)));
        actions.add(btnMyBookings);
        actions.add(Box.createRigidArea(new Dimension(0, 8)));
        actions.add(btnCarbon);

        center.add(actions);


        bookingsModel = new DefaultTableModel(new String[] { "ID", "From", "To", "Mode", "Km" }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingsTable = new JTable(bookingsModel);
        JScrollPane sp = new JScrollPane(bookingsTable);
        center.add(sp);

        p.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JLabel("Eco Travel - Manage your trips and reduce your footprint"));
        p.add(bottom, BorderLayout.SOUTH);

        btnLogout.addActionListener(e -> {
            currentUser = null;
            cards.show(cardPanel, "login");
        });

        btnPlan.addActionListener(e -> cards.show(cardPanel, "plan"));
        btnBook.addActionListener(e -> cards.show(cardPanel, "book"));
        btnMyBookings.addActionListener(e -> refreshBookingsTable());
        btnCarbon.addActionListener(e -> cards.show(cardPanel, "carbon"));

        return p;
    }

    private JPanel planTripPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Plan a Trip", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        p.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("From:"));
        JTextField tfFrom = new JTextField();
        form.add(tfFrom);
        form.add(new JLabel("To:"));
        JTextField tfTo = new JTextField();
        form.add(tfTo);
        form.add(new JLabel("Distance (km):"));
        JTextField tfKm = new JTextField();
        form.add(tfKm);
        form.add(new JLabel("Mode:"));
        String[] modes = { "carpool", "ev", "bus/train" };
        JComboBox<String> cbMode = new JComboBox<>(modes);
        form.add(cbMode);
        p.add(form, BorderLayout.CENTER);

        JPanel south = new JPanel();
        JButton btnSave = new JButton("Save Trip");
        JButton btnBack = new JButton("Back");
        south.add(btnSave);
        south.add(btnBack);
        p.add(south, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(frame, "Please login first.");
                cards.show(cardPanel, "login");
                return;
            }
            String from = tfFrom.getText().trim();
            String to = tfTo.getText().trim();
            double km = 0;
            try {
                km = Double.parseDouble(tfKm.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid distance");
                return;
            }
            String mode = (String) cbMode.getSelectedItem();
            // create Trip and save using Storage
            java.util.UUID id = java.util.UUID.randomUUID();
            Trip t = new Trip(id.toString(), currentUser.getUsername(), from, to, mode, km);
            List<Trip> trips = Storage.loadTrips();
            trips.add(t);
            Storage.saveTrips(trips);
            JOptionPane.showMessageDialog(frame, "Trip saved: " + id.toString());
            tfFrom.setText("");
            tfTo.setText("");
            tfKm.setText("");
            refreshBookingsTable();
            cards.show(cardPanel, "dashboard");
        });

        btnBack.addActionListener(e -> cards.show(cardPanel, "dashboard"));
        return p;
    }

    private JPanel bookPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Booking & Eco Options", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        p.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(0, 1, 8, 8));
        JButton btnEv = new JButton("Quick EV Rental");
        JButton btnCarpool = new JButton("Join Carpool");
        JButton btnBookSaved = new JButton("Book from Saved Trips");
        center.add(btnEv);
        center.add(btnCarpool);
        center.add(btnBookSaved);
        p.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel();
        JButton btnBack = new JButton("Back");
        south.add(btnBack);
        p.add(south, BorderLayout.SOUTH);

        btnEv.addActionListener(e -> quickBookDialog("ev"));
        btnCarpool.addActionListener(e -> quickBookDialog("carpool"));
        btnBookSaved.addActionListener(e -> bookFromSavedDialog());
        btnBack.addActionListener(e -> cards.show(cardPanel, "dashboard"));

        return p;
    }

    private JPanel carbonPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = new JLabel("Carbon Footprint Calculator", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        p.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Distance (km):"));
        JTextField tfKm = new JTextField();
        form.add(tfKm);
        form.add(new JLabel("Mode:"));
        String[] modes = { "private_car", "carpool", "ev", "bus_train" };
        JComboBox<String> cbMode = new JComboBox<>(modes);
        form.add(cbMode);
        p.add(form, BorderLayout.CENTER);

        JPanel south = new JPanel();
        JButton btnCalc = new JButton("Calculate");
        JButton btnBack = new JButton("Back");
        south.add(btnCalc);
        south.add(btnBack);
        p.add(south, BorderLayout.SOUTH);

        btnCalc.addActionListener(e -> {
            double km = 0;
            try {
                km = Double.parseDouble(tfKm.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid distance");
                return;
            }
            String mode = (String) cbMode.getSelectedItem();
            double kg = tracker.calculateCarbon(km, mode);
            String message = String.format("Estimated emissions for %.2f km using %s: %.2f kg CO2e", km, mode, kg);
            JOptionPane.showMessageDialog(frame, message);
        });

        btnBack.addActionListener(e -> cards.show(cardPanel, "dashboard"));
        return p;
    }

    // Helpers
    private User attemptLogin(String username, String pass) {
        List<User> users = Storage.loadUsers();
        String h = sha256(pass);
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username) && u.getPasswordHash().equals(h))
                return u;
        }
        return null;
    }

    private void quickBookDialog(String mode) {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(frame, "Please login first.");
            cards.show(cardPanel, "login");
            return;
        }
        JTextField tfFrom = new JTextField();
        JTextField tfTo = new JTextField();
        JTextField tfKm = new JTextField();
        Object[] fields = { "From:", tfFrom, "To:", tfTo, "Distance km:", tfKm };
        int r = JOptionPane.showConfirmDialog(frame, fields, "Quick book - " + mode, JOptionPane.OK_CANCEL_OPTION);
        if (r == JOptionPane.OK_OPTION) {
            try {
                double km = Double.parseDouble(tfKm.getText().trim());
                java.util.UUID id = java.util.UUID.randomUUID();
                Trip t = new Trip(id.toString(), currentUser.getUsername(), tfFrom.getText().trim(),
                        tfTo.getText().trim(), mode, km);
                List<Trip> trips = Storage.loadTrips();
                trips.add(t);
                Storage.saveTrips(trips);
                JOptionPane.showMessageDialog(frame, "Booked " + mode + ", ID: " + id);
                refreshBookingsTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid distance");
            }
        }
    }

    private void bookFromSavedDialog() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(frame, "Please login first.");
            cards.show(cardPanel, "login");
            return;
        }
        List<Trip> trips = Storage.loadTrips();
        Vector<String> ids = new Vector<>();
        for (Trip t : trips) {
            if (t.getUsername().equals(currentUser.getUsername()))
                ids.add(t.getId());
        }
        if (ids.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No saved trips");
            return;
        }
        String sel = (String) JOptionPane.showInputDialog(frame, "Select trip ID to confirm booking:",
                "Book Saved Trip", JOptionPane.PLAIN_MESSAGE, null, ids.toArray(), ids.get(0));
        if (sel != null) {
            JOptionPane.showMessageDialog(frame, "Booking confirmed for " + sel);
        }
    }

    private void refreshBookingsTable() {
        bookingsModel.setRowCount(0);
        if (currentUser == null)
            return;
        List<Trip> trips = Storage.loadTrips();
        for (Trip t : trips) {
            if (t.getUsername().equals(currentUser.getUsername())) {
                bookingsModel.addRow(
                        new Object[] { t.getId(), t.getFrom(), t.getTo(), t.getTransportMode(), t.getDistanceKm() });
            }
        }
    }

    private String sha256(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte x : b)
                sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception e) {
            return input;
        }
    }

    public static void main(String[] args) {
        // ensure Swing runs on EDT
        SwingUtilities.invokeLater(() -> new EcoTravel_Swing_UI());
    }
}
