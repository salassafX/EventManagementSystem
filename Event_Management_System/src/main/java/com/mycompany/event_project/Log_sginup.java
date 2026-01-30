package com.mycompany.event_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Log_sginup extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, signupButton;

    public Log_sginup() {

        setTitle("Event System - Login");
        setSize(360, 230);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Welcome to Event System", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel passLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        centerPanel.add(emailLabel);
        centerPanel.add(emailField);
        centerPanel.add(Box.createVerticalStrut(12));

        centerPanel.add(passLabel);
        centerPanel.add(passwordField);
        centerPanel.add(Box.createVerticalStrut(18));

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        loginButton = new JButton("Login");
        signupButton = new JButton("Sign Up");
        bottomPanel.add(loginButton);
        bottomPanel.add(signupButton);

        add(bottomPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(new LoginAction());

        signupButton.addActionListener(e -> {
            new Signup();
            dispose();
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            String email = emailField.getText();
            String pass = new String(passwordField.getPassword());

            if (email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill all fields!");
                return;
            }

            try (Connection con = DB.getConnection()) {

                String sql = "SELECT * FROM users WHERE email=? AND password=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, email);
                ps.setString(2, pass);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {

                    int userId = rs.getInt("user_id");
                    String role = rs.getString("role");

                    JOptionPane.showMessageDialog(null, "Login Successful!", "Success", JOptionPane.PLAIN_MESSAGE);

                    switch (role) {
                        case "ADMIN":
                            new Admin();
                            break;
                        case "ORGANIZER":
                            new Organizer(userId);
                            break;
                        case "ATTENDEE":
                            new Attende(userId);
                            break;
                    }

                    dispose();

                } else {
                    JOptionPane.showMessageDialog(null, "Invalid email or password!");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
            }
        }
    }
}