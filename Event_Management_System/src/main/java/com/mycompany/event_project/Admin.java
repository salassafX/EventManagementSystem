package com.mycompany.event_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class Admin extends JFrame {

    private JTable eventTable, userTable;
    private JButton refreshEventsBtn, addEventBtn, deleteEventBtn, editEventBtn;
    private JButton refreshUsersBtn, addOrganizerBtn, deleteUserBtn;
    private JTextArea reportArea;

    // Tabbed layout for main sections (events / users / reports)
    private JTabbedPane tabbedPane;

    public Admin() {
        setTitle("Event Management - Admin");
        setSize(960, 660);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ================= UI DESIGN =================

        // Root panel
        JPanel root = new JPanel(new BorderLayout(5, 5));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        // Simple header label
        JLabel headerLabel = new JLabel("Admin - Event Management", SwingConstants.CENTER);
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 18f));
        headerLabel.setBorder(new EmptyBorder(5, 5, 10, 5));
        root.add(headerLabel, BorderLayout.NORTH);

        // Left tabbed pane (similar to screenshot)
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        JPanel eventsPanel = createEventsPanel();
        JPanel usersPanel = createUsersPanel();
        JPanel reportsPanel = createReportsPanel();

        tabbedPane.addTab("Events", eventsPanel);
        tabbedPane.addTab("Users", usersPanel);
        tabbedPane.addTab("Reports", reportsPanel);

        root.add(tabbedPane, BorderLayout.CENTER);

        // ================ END UI DESIGN ===============

        // Initial data load
        loadEvents();
        loadUsers();

        setVisible(true);
    }

    // ======= Events tab panel (table + vertical buttons on the right) =======
    private JPanel createEventsPanel() {

        JPanel eventsPanel = new JPanel(new BorderLayout(8, 8));

        JLabel title = new JLabel("My Events");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        title.setBorder(new EmptyBorder(5, 5, 5, 5));
        eventsPanel.add(title, BorderLayout.NORTH);

        // Events table in the center
        eventTable = new JTable();
        JScrollPane eventScroll = new JScrollPane(eventTable);
        eventsPanel.add(eventScroll, BorderLayout.CENTER);

        // Buttons column on the right
        JPanel eventBtns = new JPanel();
        eventBtns.setLayout(new BoxLayout(eventBtns, BoxLayout.Y_AXIS));
        eventBtns.setBorder(new EmptyBorder(10, 10, 10, 10));

        // All buttons use the same helper, so same size and style
        refreshEventsBtn = createMainActionButton("Refresh Events");
        addEventBtn       = createMainActionButton("Add Event");
        editEventBtn      = createMainActionButton("Edit Event");
        deleteEventBtn    = createMainActionButton("Delete Event");

        eventBtns.add(refreshEventsBtn);
        eventBtns.add(Box.createVerticalStrut(10));
        eventBtns.add(addEventBtn);
        eventBtns.add(Box.createVerticalStrut(10));
        eventBtns.add(editEventBtn);
        eventBtns.add(Box.createVerticalStrut(10));
        eventBtns.add(deleteEventBtn);
        eventBtns.add(Box.createVerticalGlue());

        eventsPanel.add(eventBtns, BorderLayout.EAST);

        // Wire actions to existing methods
        refreshEventsBtn.addActionListener(e -> loadEvents());
        addEventBtn.addActionListener(e -> addEvent());
        editEventBtn.addActionListener(e -> editEvent());
        deleteEventBtn.addActionListener(e -> deleteEvent());

        return eventsPanel;
    }

    // ======= Users tab panel (table + vertical buttons on the right) =======
    private JPanel createUsersPanel() {

        JPanel usersPanel = new JPanel(new BorderLayout(8, 8));

        JLabel title = new JLabel("Users & Organizers");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        title.setBorder(new EmptyBorder(5, 5, 5, 5));
        usersPanel.add(title, BorderLayout.NORTH);

        userTable = new JTable();
        JScrollPane userScroll = new JScrollPane(userTable);
        usersPanel.add(userScroll, BorderLayout.CENTER);

        JPanel userBtns = new JPanel();
        userBtns.setLayout(new BoxLayout(userBtns, BoxLayout.Y_AXIS));
        userBtns.setBorder(new EmptyBorder(10, 10, 10, 10));

        refreshUsersBtn  = createMainActionButton("Refresh Users");
        addOrganizerBtn  = createMainActionButton("Add Organizer");
        deleteUserBtn    = createMainActionButton("Delete User");

        userBtns.add(refreshUsersBtn);
        userBtns.add(Box.createVerticalStrut(10));
        userBtns.add(addOrganizerBtn);
        userBtns.add(Box.createVerticalStrut(10));
        userBtns.add(deleteUserBtn);
        userBtns.add(Box.createVerticalGlue());

        usersPanel.add(userBtns, BorderLayout.EAST);

        refreshUsersBtn.addActionListener(e -> loadUsers());
        addOrganizerBtn.addActionListener(e -> addOrganizer());
        deleteUserBtn.addActionListener(e -> deleteUser());

        return usersPanel;
    }

    // ======= Reports tab panel (report text + button on the right) =======
    private JPanel createReportsPanel() {

        JPanel reportsPanel = new JPanel(new BorderLayout(8, 8));

        JLabel title = new JLabel("System Reports");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        title.setBorder(new EmptyBorder(5, 5, 5, 5));
        reportsPanel.add(title, BorderLayout.NORTH);

        // Multi-line text area to display the generated report
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        reportArea.setMargin(new Insets(8, 8, 8, 8));

        JScrollPane reportScroll = new JScrollPane(reportArea);
        reportsPanel.add(reportScroll, BorderLayout.CENTER);

        // Buttons column on the right
        JPanel reportBtnPanel = new JPanel();
        reportBtnPanel.setLayout(new BoxLayout(reportBtnPanel, BoxLayout.Y_AXIS));
        reportBtnPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton generateReportBtn = createMainActionButton("Generate Report");
        reportBtnPanel.add(generateReportBtn);
        reportBtnPanel.add(Box.createVerticalGlue());

        reportsPanel.add(reportBtnPanel, BorderLayout.EAST);

        // Generate report when button is clicked
        generateReportBtn.addActionListener(e -> generateReport());

        return reportsPanel;
    }

    // ======= Button helpers (same size, no custom colors) =======
    private JButton createMainActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);

        
        Dimension size = new Dimension(180, 40);
        btn.setPreferredSize(size);
        btn.setMaximumSize(size);
        btn.setMinimumSize(size);

        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        return btn;
    }

    // These two are kept for compatibility if you used them somewhere else.
    // Currently they just call the main helper so style stays identical.
    private JButton createSecondaryActionButton(String text) {
        return createMainActionButton(text);
    }

    private JButton createDangerActionButton(String text) {
        return createMainActionButton(text);
    }


    // ======================= Business logic methods  =======================

    // Load all events from the database into the events table
    private void loadEvents() {

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Title", "Category", "Location", "Date", "Capacity"}, 0
        );

        String sql = "SELECT * FROM events ORDER BY event_date ASC";

        try (Connection con = DB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("event_id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getString("location"),
                        rs.getString("event_date"),
                        rs.getInt("seat_capacity")
                });
            }

            eventTable.setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading events: " + ex.getMessage());
        }
    }

    // Load all users from the database into the users table
    private void loadUsers() {

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Full Name", "Email", "Role"}, 0
        );

        try (Connection con = DB.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT user_id, full_name, email, role FROM users")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("role")
                });
            }

            userTable.setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        }
    }

    // Create a new event by collecting data via dialogs and inserting into DB
    private void addEvent() {

        String title = "";
        String category = "";
        String location = "";
        String date = "";
        String capacityTxt = "";

        // ===== TITLE =====
        while (true) {
            title = JOptionPane.showInputDialog("Event Title:");
            if (title == null) {
                JOptionPane.showMessageDialog(this, "Operation cancelled!");
                return;
            }
            title = title.trim();
            if (!title.isEmpty()) break;
            JOptionPane.showMessageDialog(this, "The title field is required!");
        }

        // ===== CATEGORY =====
        while (true) {
            category = JOptionPane.showInputDialog("Category:");
            if (category == null) {
                JOptionPane.showMessageDialog(this, "Operation cancelled!");
                return;
            }
            category = category.trim();
            if (!category.isEmpty()) break;
            JOptionPane.showMessageDialog(this, "The category field is required!");
        }

        // ===== LOCATION =====
        while (true) {
            location = JOptionPane.showInputDialog("Location:");
            if (location == null) {
                JOptionPane.showMessageDialog(this, "Operation cancelled!");
                return;
            }
            location = location.trim();
            if (!location.isEmpty()) break;
            JOptionPane.showMessageDialog(this, "The Location field is required!");
        }

        // ===== DATE =====
        while (true) {
            date = JOptionPane.showInputDialog("Event Date (YYYY-MM-DD HH:MM:SS):");
            if (date == null) {
                JOptionPane.showMessageDialog(this, "Operation cancelled!");
                return;
            }
            date = date.trim();
            if (!date.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                JOptionPane.showMessageDialog(this, "The date format is not valid!");
                continue;
            }
            break;
        }

        // ===== CAPACITY =====
        int capacity = 0;
        while (true) {
            capacityTxt = JOptionPane.showInputDialog("Capacity:");
            if (capacityTxt == null) {
                JOptionPane.showMessageDialog(this, "Operation cancelled!");
                return;
            }
            capacityTxt = capacityTxt.trim();
            try {
                capacity = Integer.parseInt(capacityTxt);
                if (capacity <= 0) {
                    JOptionPane.showMessageDialog(this, "Capacity should be positive!");
                    continue;
                }
                break; // Correct input → exit loop
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Capacity should be a number!");
            }
        }

        // Insert to DB
        String sql = "INSERT INTO events (title, category, location, event_date, seat_capacity, organizer_id) VALUES (?, ?, ?, ?, ?, 2)";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, category);
            ps.setString(3, location);
            ps.setString(4, date);
            ps.setInt(5, capacity);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Event Added!");
            loadEvents();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding event: " + ex.getMessage());
        }
    }

    // Edit selected event with validation and proper dialogs
    private void editEvent() {

        // 1) Make sure an event is selected
        int row = eventTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an event to edit!");
            return;
        }

        // 2) Read current values from the table
        int eventId = (int) eventTable.getValueAt(row, 0);
        String oldTitle = (String) eventTable.getValueAt(row, 1);
        String oldCategory = (String) eventTable.getValueAt(row, 2);
        String oldLocation = (String) eventTable.getValueAt(row, 3);
        String oldDate = String.valueOf(eventTable.getValueAt(row, 4));
        int oldCapacity = (int) eventTable.getValueAt(row, 5);

        String title = oldTitle;
        String category = oldCategory;
        String location = oldLocation;
        String date = oldDate;
        String capacityTxt;
        int capacity = oldCapacity;

        // ===== TITLE =====
        while (true) {
            String input = (String) JOptionPane.showInputDialog(
                    this,
                    "Event Title:",
                    "Edit Event",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    title  // current value as default
            );
            if (input == null) {
                JOptionPane.showMessageDialog(this, "Edit cancelled!");
                return;
            }
            input = input.trim();
            if (!input.isEmpty()) {
                title = input;
                break;
            }
            JOptionPane.showMessageDialog(this, "The title field is required!");
        }

        // ===== CATEGORY =====
        while (true) {
            String input = (String) JOptionPane.showInputDialog(
                    this,
                    "Category:",
                    "Edit Event",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    category
            );
            if (input == null) {
                JOptionPane.showMessageDialog(this, "Edit cancelled!");
                return;
            }
            input = input.trim();
            if (!input.isEmpty()) {
                category = input;
                break;
            }
            JOptionPane.showMessageDialog(this, "The category field is required!");
        }

        // ===== LOCATION =====
        while (true) {
            String input = (String) JOptionPane.showInputDialog(
                    this,
                    "Location:",
                    "Edit Event",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    location
            );
            if (input == null) {
                JOptionPane.showMessageDialog(this, "Edit cancelled!");
                return;
            }
            input = input.trim();
            if (!input.isEmpty()) {
                location = input;
                break;
            }
            JOptionPane.showMessageDialog(this, "The Location field is required!");
        }

        // ===== DATE =====
        while (true) {
            String input = (String) JOptionPane.showInputDialog(
                    this,
                    "Event Date (YYYY-MM-DD HH:MM:SS):",
                    "Edit Event",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    date
            );
            if (input == null) {
                JOptionPane.showMessageDialog(this, "Edit cancelled!");
                return;
            }
            input = input.trim();
            if (!input.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                JOptionPane.showMessageDialog(this, "The date format is not valid!");
                continue;
            }
            date = input;
            break;
        }

        // ===== CAPACITY =====
        while (true) {
            capacityTxt = (String) JOptionPane.showInputDialog(
                    this,
                    "Capacity:",
                    "Edit Event",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    String.valueOf(capacity)
            );
            if (capacityTxt == null) {
                JOptionPane.showMessageDialog(this, "Edit cancelled!");
                return;
            }
            capacityTxt = capacityTxt.trim();
            try {
                int parsed = Integer.parseInt(capacityTxt);
                if (parsed <= 0) {
                    JOptionPane.showMessageDialog(this, "Capacity should be positive!");
                    continue;
                }
                capacity = parsed;
                break;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Capacity should be a number!");
            }
        }

        // 3) Confirm update
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Save changes to this event?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Edit cancelled.");
            return;
        }

        // 4) Update in DB
        String sql = "UPDATE events " +
                "SET title = ?, category = ?, location = ?, event_date = ?, seat_capacity = ? " +
                "WHERE event_id = ?";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, category);
            ps.setString(3, location);
            ps.setString(4, date);
            ps.setInt(5, capacity);
            ps.setInt(6, eventId);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Event updated successfully!");
            loadEvents(); // reload table

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating event: " + ex.getMessage());
        }
    }

    // Delete the selected event after checks
    private void deleteEvent() {

        int row = eventTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an event!");
            return;
        }

        int id = (int) eventTable.getValueAt(row, 0);

        // Check if the event has any registrations before deleting
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM registrations WHERE event_id=?")) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete this event because users are registered.",
                        "Blocked",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error checking event: " + ex.getMessage());
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this event?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM events WHERE event_id=?")) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Event deleted!");
            loadEvents();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting event: " + ex.getMessage());
        }
    }

    // Delete selected user with business rules
    private void deleteUser() {

        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user!");
            return;
        }

        int userId = (int) userTable.getValueAt(row, 0);
        String role = (String) userTable.getValueAt(row, 3);

        // Prevent deleting the only admin in the system
        if (role.equalsIgnoreCase("admin")) {
            try (Connection con = DB.getConnection();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) AS c FROM users WHERE role='admin'")) {

                rs.next();
                if (rs.getInt("c") <= 1) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot delete the only admin!", "Blocked", JOptionPane.ERROR_MESSAGE);
                    return;
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                return;
            }
        }

        // Prevent deleting users who have event registrations
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM registrations WHERE user_id=?")) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this,
                        "User has registrations and cannot be deleted!",
                        "Blocked", JOptionPane.ERROR_MESSAGE);
                return;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this user?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM users WHERE user_id=?")) {

            ps.setInt(1, userId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "User deleted!");
            loadUsers();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage());
        }
    }

    // Add a new organizer with validation and duplicate email check
    private void addOrganizer() {

        String fullName = JOptionPane.showInputDialog("Organizer Full Name:");
        String email = JOptionPane.showInputDialog("Email:");
        String password = JOptionPane.showInputDialog("Password:");

        // User cancelled any of the dialogs
        if (fullName == null || email == null || password == null) {
            JOptionPane.showMessageDialog(this, "Operation cancelled!");
            return;
        }

        fullName = fullName.trim();
        email = email.trim();
        password = password.trim();

        // Basic field validation
        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        // Simple email format check
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format!");
            return;
        }

        // Enforce a minimal password length
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "Password too short!");
            return;
        }

        // Check if email already exists in the system
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE email=?")) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Email already exists!");
                return;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error checking email: " + ex.getMessage());
            return;
        }

        // Insert new organizer user
        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO users (full_name, email, password, role) VALUES (?, ?, ?, 'ORGANIZER')"
             )) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, password);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Organizer added!");
            loadUsers();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding organizer: " + ex.getMessage());
        }
    }

private void generateReport() {
    StringBuilder r = new StringBuilder();
    r.append("===== EVENT SUMMARY REPORT =====\n\n");

    try (Connection con = DB.getConnection();
         Statement st = con.createStatement()) {

        // 1) Total registrations per event + capacity + waitlist
        r.append("1) Registrations per Event (with Capacity & Waitlist)\n");
        r.append("-----------------------------------------------------\n");

        ResultSet rs = st.executeQuery(
            "SELECT e.title, e.category, e.seat_capacity, " +
            "       COALESCE(SUM(CASE WHEN r.status = 'REGISTERED' THEN 1 ELSE 0 END), 0) AS confirmed, " +
            "       COALESCE(SUM(CASE WHEN r.status = 'WAITLIST'  THEN 1 ELSE 0 END), 0) AS waitlist " +
            "FROM events e " +
            "LEFT JOIN registrations r ON e.event_id = r.event_id " +
            "GROUP BY e.event_id, e.title, e.category, e.seat_capacity " +
            "ORDER BY confirmed DESC"
        );

        while (rs.next()) {
            String title    = rs.getString("title");
            String category = rs.getString("category");
            int capacity    = rs.getInt("seat_capacity");
            int confirmed   = rs.getInt("confirmed");
            int waitlist    = rs.getInt("waitlist");

            double util = (capacity > 0)
                    ? (confirmed * 100.0 / capacity)
                    : 0.0;

            r.append(title).append(" (").append(category).append(")\n");
            r.append("  Confirmed : ").append(confirmed).append("\n");
            r.append("  Waitlist  : ").append(waitlist).append("\n");
            r.append("  Capacity  : ").append(capacity)
             .append(" | Utilization: ")
             .append(String.format("%.1f", util)).append("%\n\n");
        }

        // 2) Most attended event category (by REGISTERED)
        r.append("2) Most Attended Category\n");
        r.append("--------------------------\n");

        ResultSet rsCat = st.executeQuery(
            "SELECT e.category, COUNT(r.registration_id) AS regCount " +
            "FROM events e " +
            "JOIN registrations r ON e.event_id = r.event_id " +
            "WHERE r.status = 'REGISTERED' " +
            "GROUP BY e.category " +
            "ORDER BY regCount DESC " +
            "LIMIT 1"
        );

        if (rsCat.next()) {
            r.append("Category      : ").append(rsCat.getString("category")).append("\n");
            r.append("Registrations : ").append(rsCat.getInt("regCount")).append("\n\n");
        } else {
            r.append("No registrations found.\n\n");
        }

        r.append("===== END OF REPORT =====\n");

        reportArea.setText(r.toString());

        JOptionPane.showMessageDialog(
                this,
                "Report generated successfully!",
                "Success",
                JOptionPane.PLAIN_MESSAGE
        );

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage());
    }
}

}
