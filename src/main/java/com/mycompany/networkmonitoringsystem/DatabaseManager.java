package com.mycompany.networkmonitoringsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central place for database connections.
 * Credentials come from environment variables so they are never committed to Git:
 *   DB_URL      (default: jdbc:postgresql://localhost:5432/network_monitoring)
 *   DB_USER     (default: postgres)
 *   DB_PASSWORD (required)
 */
public class DatabaseManager {

    private static final String URL = env("DB_URL", "jdbc:postgresql://localhost:5432/network_monitoring");
    private static final String USER = env("DB_USER", "postgres");
    private static final String PASS = env("DB_PASSWORD", "");

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
