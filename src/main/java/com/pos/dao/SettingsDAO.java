package com.pos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SettingsDAO {
    public Map<String, String> getAll() throws SQLException {
        Map<String, String> map = new HashMap<>();
        String sql = "SELECT setting_key, setting_value FROM company_settings";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("setting_key"), rs.getString("setting_value"));
            }
        }
        return map;
    }

    public void upsert(String key, String value) throws SQLException {
        String sql = """
                MERGE INTO company_settings(setting_key, setting_value)
                KEY(setting_key)
                VALUES(?,?)
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.executeUpdate();
        }
    }
}
