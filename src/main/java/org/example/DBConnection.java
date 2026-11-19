package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Gives a fresh connection to MySQL whenever it is called
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/ecommerce_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Tusharinkiet@25";   // <-- change this

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}