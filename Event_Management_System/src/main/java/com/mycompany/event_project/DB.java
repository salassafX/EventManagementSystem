
package com.mycompany.event_project;

import java.sql.Connection;
import java.sql.DriverManager;

public class DB {

    private static final String URL = "jdbc:mysql://localhost:3306/event";
    private static final String USER = "root";
    private static final String PASS = "1234"; 

    public static Connection getConnection() {
        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("done");

        } catch (Exception e) {
            System.out.println("Connection Error: " + e.getMessage());
        }

        return con;
    }
}
