package com.mycompany.event_project;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/event?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "111223344";

    public static Connection getConnection() {
        try {
            Connection con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("DB CONNECTED ✅");
            return con;
        } catch (Exception e) {
            e.printStackTrace();   // لا تحذفيه
            return null;
        }
    }
}