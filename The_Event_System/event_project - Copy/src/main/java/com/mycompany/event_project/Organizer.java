package com.mycompany.event_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Organizer extends JFrame {

    private JTable eventTable, attendeeTable;
    private JButton refreshEventsBtn, addEventBtn, editEventBtn, deleteEventBtn, loadAttendeesBtn;

    private int organizerId;  
    public Organizer(int organizerId) {
    this.organizerId = organizerId;

    setTitle("Organizer");
    setSize(1000, 650); 
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

 
    setLayout(new BorderLayout());


    JTabbedPane tabs = new JTabbedPane();
    tabs.setTabPlacement(JTabbedPane.LEFT);
    
    
    Font tabFont = new Font("Arial", Font.BOLD, 14);
    UIManager.put("TabbedPane.font", tabFont);

   
    JPanel eventsPanel = new JPanel(new BorderLayout(5, 5));
    
   
    JPanel eventRightPanel = new JPanel();
    eventRightPanel.setLayout(new BoxLayout(eventRightPanel, BoxLayout.Y_AXIS));
    eventRightPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));
    
    refreshEventsBtn = new JButton("Refresh Events");
    addEventBtn = new JButton("Add Event");
    editEventBtn = new JButton("Edit Event");
    deleteEventBtn = new JButton("Delete Event");
    
    
    Dimension buttonSize = new Dimension(150, 40);
    refreshEventsBtn.setPreferredSize(buttonSize);
    refreshEventsBtn.setMaximumSize(buttonSize);
    addEventBtn.setPreferredSize(buttonSize);
    addEventBtn.setMaximumSize(buttonSize);
    editEventBtn.setPreferredSize(buttonSize);
    editEventBtn.setMaximumSize(buttonSize);
    deleteEventBtn.setPreferredSize(buttonSize);
    deleteEventBtn.setMaximumSize(buttonSize);
    
 
    eventRightPanel.add(Box.createVerticalStrut(10));
    eventRightPanel.add(refreshEventsBtn);
    eventRightPanel.add(Box.createVerticalStrut(15));
    eventRightPanel.add(addEventBtn);
    eventRightPanel.add(Box.createVerticalStrut(15));
    eventRightPanel.add(editEventBtn);
    eventRightPanel.add(Box.createVerticalStrut(15));
    eventRightPanel.add(deleteEventBtn);
    eventRightPanel.add(Box.createVerticalGlue());
    
   
    eventTable = new JTable();
    JScrollPane eventScroll = new JScrollPane(eventTable);
    
   
    JPanel eventsContentPanel = new JPanel(new BorderLayout());
    eventsContentPanel.add(eventScroll, BorderLayout.CENTER);
    eventsContentPanel.add(eventRightPanel, BorderLayout.EAST);
    
    eventsPanel.add(eventsContentPanel, BorderLayout.CENTER);
    tabs.add("My Events", eventsPanel);

  
    JPanel attendeesPanel = new JPanel(new BorderLayout(5, 5));
    
   
    JPanel attendeeRightPanel = new JPanel();
    attendeeRightPanel.setLayout(new BoxLayout(attendeeRightPanel, BoxLayout.Y_AXIS));
    attendeeRightPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));
    
    loadAttendeesBtn = new JButton("Load Attendees");
    loadAttendeesBtn.setPreferredSize(buttonSize);
    loadAttendeesBtn.setMaximumSize(buttonSize);
    
    attendeeRightPanel.add(Box.createVerticalStrut(10));
    attendeeRightPanel.add(loadAttendeesBtn);
    attendeeRightPanel.add(Box.createVerticalGlue());
    
   
    attendeeTable = new JTable();
    JScrollPane attendeeScroll = new JScrollPane(attendeeTable);
    
   
    JPanel attendeesContentPanel = new JPanel(new BorderLayout());
    attendeesContentPanel.add(attendeeScroll, BorderLayout.CENTER);
    attendeesContentPanel.add(attendeeRightPanel, BorderLayout.EAST);
    
    attendeesPanel.add(attendeesContentPanel, BorderLayout.CENTER);
    tabs.add("Event Attendees", attendeesPanel);

    
    add(tabs, BorderLayout.CENTER);

  
    refreshEventsBtn.addActionListener(e -> loadEvents());
    addEventBtn.addActionListener(e -> addEvent());
    editEventBtn.addActionListener(e -> editEvent());
    deleteEventBtn.addActionListener(e -> deleteEvent());
    loadAttendeesBtn.addActionListener(e -> loadAttendees());

    loadEvents();

    setLocationRelativeTo(null);
    setVisible(true);
}
  
    private boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean isValidDateFormat(String date) {
        
        return date.matches("\\d{4}-\\d{2}-\\d{2}.*");
    }

    
   private void loadEvents() {
    DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Title", "Type", "Location", "Date", "Capacity", "Registrations"}, 0
    );

    String sql =
            "SELECT e.event_id, e.title, e.category, e.location, e.event_date, e.seat_capacity, " +
            "       (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.event_id) AS reg_count " +
            "FROM events e WHERE e.organizer_id = ?";

    try (Connection con = DB.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, organizerId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[]{
                    rs.getInt("event_id"),
                    rs.getString("title"),
                    rs.getString("category"),
                    rs.getString("location"),
                    rs.getString("event_date"),
                    rs.getInt("seat_capacity"),
                    rs.getInt("reg_count")    
            });
        }

        eventTable.setModel(model);

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                "Error loading events:\n" + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
    // ---------------------
    // ADD EVENT
    // ---------------------
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

    // ---------------------
    // EDIT EVENT
    // ---------------------
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
    // ---------------------
    // DELETE EVENT
    // ---------------------
    private void deleteEvent() {
        int row = eventTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select an event to delete",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) eventTable.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete ?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DB.getConnection()) {

           
            try (PreparedStatement psReg = con.prepareStatement(
                    "DELETE FROM registrations WHERE event_id = ?")) {
                psReg.setInt(1, id);
                psReg.executeUpdate();
            }

            
            try (PreparedStatement psEvt = con.prepareStatement(
                    "DELETE FROM events WHERE event_id = ?")) {
                psEvt.setInt(1, id);
                psEvt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this,
                    "Event and its registrations deleted successfully!");
            loadEvents();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting event:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------------
    // LOAD ATTENDEES
    // ---------------------
    private void loadAttendees() {

        int row = eventTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select an event!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = (int) eventTable.getValueAt(row, 0);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Reg ID", "Name", "Email", "Status"}, 0
        );

        String sql =
                "SELECT r.registration_id, u.full_name, u.email, r.status " +
                "FROM registrations r JOIN users u ON r.user_id = u.user_id " +
                "WHERE r.event_id = ?";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("registration_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("status")
                });
            }

            attendeeTable.setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading :\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
