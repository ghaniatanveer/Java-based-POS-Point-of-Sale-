package com.pos.dao;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static final String DB_URL = "jdbc:h2:file:./database/posdb;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASS = "";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Files.createDirectories(Path.of("database"));
        } catch (Exception e) {
            throw new SQLException("Failed to prepare database directory", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
