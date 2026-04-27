package com.pos.dao;

import com.pos.models.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    public List<Customer> findAll() throws SQLException {
        List<Customer> data = new ArrayList<>();
        String sql = "SELECT id, full_name, phone, email FROM customers ORDER BY full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                data.add(new Customer(rs.getInt("id"), rs.getString("full_name"), rs.getString("phone"), rs.getString("email")));
            }
        }
        return data;
    }
}
