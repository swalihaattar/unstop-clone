package com.unstop.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Utility class for JDBC database connectivity.
 *
 * WHY: All DAOs call this single class to get a connection.
 * If the DB URL changes, we only change it here - one place.
 *
 * PATTERN: Simple factory method. Not a full connection pool
 * (acceptable for a college project; use HikariCP in production).
 */
public class DBConnection {

    // Read values from OS environment variables so secrets are not committed to Git.
    // Values are still available as safe local defaults for a quick setup.
    private static final String URL = System.getenv().getOrDefault(
            "DB_URL", "jdbc:mysql://localhost:3306/unstop_clone?useSSL=false&serverTimezone=UTC");
    private static final String USERNAME = System.getenv().getOrDefault("DB_USERNAME", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "root");

    // Static block: loads the MySQL driver class once when JVM starts
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver not found. Add mysql-connector-java to pom.xml", e);
        }
    }

    /**
     * Returns a new JDBC Connection.
     * Caller is responsible for closing it (use try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
