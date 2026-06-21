package com.ocir.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC";
    private static final String URL = "jdbc:mysql://localhost:3306/ocir_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initDatabase() {
        try (InputStream is = DatabaseManager.class.getResourceAsStream("db_setup.sql")) {
            if (is == null) {
                System.err.println("db_setup.sql not found in resources!");
                return;
            }
            String sql = new String(is.readAllBytes());
            try (Connection conn = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
                 Statement stmt = conn.createStatement()) {
                for (String s : sql.split(";")) {
                    String trimmed = s.trim();
                    if (!trimmed.isEmpty()) {
                        stmt.execute(trimmed);
                    }
                }
            }
            System.out.println("Database initialized successfully.");
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
