package com.mycompany.event_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Attende extends JFrame {

    private int attendeeId;
    private JTable eventsTable, ticketsTable;
    private JButton registerBtn, cancelBtn, refreshEventsBtn, refreshTicketsBtn, viewTicketBtn;

    private JComboBox<String> categoryFilter;
    private JComboBox<String> locationFilter;
    private JTextField dateFromField, dateToField;
    private JButton applyFilterBtn;

    public Attende(int attendeeId) {
        this.attendeeId = attendeeId;

        setTitle("Event Portal - Attendee");
        setSize(980, 660);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JLabel mainTitle = new JLabel("Attendee Dashboard");
        mainTitle.setFont(mainTitle.getFont().deriveFont(Font.BOLD, 20f));

        JLabel subTitle = new JLabel("Browse events and manage your registrations");
        subTitle.setFont(subTitle.getFont().deriveFont(Font.PLAIN, 12f));

        headerPanel.add(mainTitle, BorderLayout.NORTH);
        headerPanel.add(subTitle, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        JPanel eventsPanel = new JPanel(new BorderLayout(8, 8));
        eventsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JPanel filterContainer = new JPanel(new BorderLayout());
        filterContainer.setBorder(BorderFactory.createTitledBorder("Filter Events"));

        JPanel filterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        categoryFilter = new JComboBox<>();
        locationFilter = new JComboBox<>();
        dateFromField = new JTextField(10);
        dateToField = new JTextField(10);
        applyFilterBtn = new JButton("Apply Filter");

        loadFilterValues();

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        filterPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        filterPanel.add(categoryFilter, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        filterPanel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        filterPanel.add(locationFilter, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        filterPanel.add(new JLabel("From (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        filterPanel.add(dateFromField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        filterPanel.add(new JLabel("To (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        filterPanel.add(dateToField, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        filterPanel.add(applyFilterBtn, gbc);

        filterContainer.add(filterPanel, BorderLayout.CENTER);
        eventsPanel.add(filterContainer, BorderLayout.NORTH);

        eventsTable = new JTable();
        JScrollPane eventScroll = new JScrollPane(eventsTable);
        eventScroll.setBorder(BorderFactory.createTitledBorder("Available Events"));
        eventsPanel.add(eventScroll, BorderLayout.CENTER);

        JPanel eventBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        registerBtn = new JButton("Register");
        refreshEventsBtn = new JButton("Refresh");
        eventBtns.add(refreshEventsBtn);
        eventBtns.add(registerBtn);
        eventsPanel.add(eventBtns, BorderLayout.SOUTH);

        tabs.add("Browse Events", eventsPanel);
   JPanel ticketsPanel = new JPanel(new BorderLayout(8, 8));
        ticketsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        ticketsTable = new JTable();
        JScrollPane ticketsScroll = new JScrollPane(ticketsTable);
        ticketsScroll.setBorder(BorderFactory.createTitledBorder("My Registrations"));

        JPanel ticketBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        cancelBtn = new JButton("Cancel Registration");
        refreshTicketsBtn = new JButton("Refresh");
        viewTicketBtn = new JButton("View Ticket");

        ticketBtns.add(refreshTicketsBtn);
        ticketBtns.add(viewTicketBtn);
        ticketBtns.add(cancelBtn);

        ticketsPanel.add(ticketsScroll, BorderLayout.CENTER);
        ticketsPanel.add(ticketBtns, BorderLayout.SOUTH);

        tabs.add("My Tickets", ticketsPanel);

        add(tabs, BorderLayout.CENTER);

        registerBtn.addActionListener(e -> registerForEvent());
        cancelBtn.addActionListener(e -> cancelRegistration());
        refreshEventsBtn.addActionListener(e -> loadEvents());
        refreshTicketsBtn.addActionListener(e -> loadTickets());
        viewTicketBtn.addActionListener(e -> showTicketDetails());
        applyFilterBtn.addActionListener(e -> loadEvents());

        loadEvents();
        loadTickets();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadFilterValues() {
        categoryFilter.addItem("All");
        locationFilter.addItem("All");

        try (Connection con = DB.getConnection();
             Statement st = con.createStatement()) {

            ResultSet rs1 = st.executeQuery("SELECT DISTINCT category FROM events");
            while (rs1.next()) {
                categoryFilter.addItem(rs1.getString(1));
            }

            ResultSet rs2 = st.executeQuery("SELECT DISTINCT location FROM events");
            while (rs2.next()) {
                locationFilter.addItem(rs2.getString(1));
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading filters: " + ex.getMessage());
        }
    }

    private void loadEvents() {

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Title", "Category", "Location", "Date", "Capacity", "Registered"}, 0
        );

        String sql =
                "SELECT e.event_id, e.title, e.category, e.location, e.event_date, " +
                        "       e.seat_capacity, " +
                        "       (SELECT COUNT(*) FROM registrations r WHERE r.event_id = e.event_id) AS registeredCount " +
                        "FROM events e WHERE 1=1 ";

        if (!categoryFilter.getSelectedItem().toString().equals("All")) {
            sql += " AND e.category = '" + categoryFilter.getSelectedItem() + "'";
        }

        if (!locationFilter.getSelectedItem().toString().equals("All")) {
            sql += " AND e.location = '" + locationFilter.getSelectedItem() + "'";
        }

        if (!dateFromField.getText().trim().isEmpty()) {
            sql += " AND e.event_date >= '" + dateFromField.getText().trim() + " 00:00:00'";
        }

        if (!dateToField.getText().trim().isEmpty()) {
            sql += " AND e.event_date <= '" + dateToField.getText().trim() + " 23:59:59'";
        }

        sql += " ORDER BY e.event_date ASC";

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
                        rs.getInt("seat_capacity"),
                        rs.getInt("registeredCount")
                });
            }

            
           eventsTable.setModel(model);
           } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading events: " + ex.getMessage());
        }
    }

    private void registerForEvent() {

        int row = eventsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, " You didn't select,Please select an event!");
            return;
        }

        int eventId = (int) eventsTable.getValueAt(row, 0);
        int capacity = (int) eventsTable.getValueAt(row, 5);
        int registered = (int) eventsTable.getValueAt(row, 6);

        if (registered >= capacity) {
            JOptionPane.showMessageDialog(this, "you Cant Book This event is full!.");
            return;
        }

        try (Connection con = DB.getConnection();
             PreparedStatement check = con.prepareStatement(
                     "SELECT * FROM registrations WHERE user_id = ? AND event_id = ?")) {

            check.setInt(1, attendeeId);
            check.setInt(2, eventId);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "You are already registered!!");
                return;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you Sure you want to Register for this event?");
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DB.getConnection()) {

            PreparedStatement reg = con.prepareStatement(
                    "INSERT INTO registrations (event_id, user_id, status) VALUES (?, ?, 'REGISTERED')",
                    Statement.RETURN_GENERATED_KEYS
            );

            reg.setInt(1, eventId);
            reg.setInt(2, attendeeId);
            reg.executeUpdate();

            ResultSet keys = reg.getGeneratedKeys();
            keys.next();
            int regId = keys.getInt(1);

            String ticketCode = "TCK-" + regId + "-" + System.currentTimeMillis();

            PreparedStatement t = con.prepareStatement(
                    "INSERT INTO tickets (registration_id, ticket_code, status) VALUES (?, ?, 'ACTIVE')"
            );

            t.setInt(1, regId);
            t.setString(2, ticketCode);
            t.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "You Registered Successfully!Ticket: " + ticketCode);

            loadEvents();
            loadTickets();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadTickets() {

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Registration ID", "Event Title", "Date", "Status"}, 0
        );

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT r.registration_id, e.title, e.event_date, r.status " +
                             "FROM registrations r JOIN events e ON r.event_id = e.event_id " +
                             "WHERE r.user_id = ? ORDER BY e.event_date")) {

            ps.setInt(1, attendeeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("registration_id"),
                        rs.getString("title"),
                        rs.getString("event_date"),
                        rs.getString("status")
                });
            }

            ticketsTable.setModel(model);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading tickets: " + ex.getMessage());
        }
    }

    private void cancelRegistration() {
        int row = ticketsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, " Please Select a ticket first!");
            return;
        }
        int regId = (int) ticketsTable.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to Cancel this registration?");
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM registrations WHERE registration_id = ?")) {

            ps.setInt(1, regId);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registration is canceled .");

            loadTickets();
            loadEvents();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error canceling: " + ex.getMessage());
        }
    }

    private void showTicketDetails() {

        int row = ticketsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a ticket!");
            return;
        }

        int regId = (int) ticketsTable.getValueAt(row, 0);

        String sql =
                "SELECT r.registration_id, e.title, e.location, e.category, e.event_date, " +
                        "       t.ticket_code, t.status " +
                        "FROM registrations r " +
                        "JOIN events e ON r.event_id = e.event_id " +
                        "JOIN tickets t ON r.registration_id = t.registration_id " +
                        "WHERE r.registration_id = ?";

        try (Connection con = DB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, regId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String msg =
                        "------ EVENT TICKET ------" +  "Event: " + rs.getString("title") + "" + "Category: " + rs.getString("category") + "" +      "Location: " + rs.getString("location") + "" + "Date: " + rs.getString("event_date") + "Ticket ID:" + rs.getString("ticket_code") + "" +
                                "Status: " + rs.getString("status");

                JOptionPane.showMessageDialog(this, msg,
                        "Ticket Details", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading ticket: " + ex.getMessage());
        }
    }
}