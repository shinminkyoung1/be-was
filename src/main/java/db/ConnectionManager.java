package db;

import webserver.config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    public static Connection getConnection() {
        try {
            Class.forName("org.h2.Driver");
            return DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PW);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to Connect SQL: " + e.getMessage());
        }
    }
}