package com.pos.utils;

import com.pos.dao.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {
    private DatabaseInitializer() {
    }

    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection(); Statement st = conn.createStatement()) {
            st.execute("""
                    CREATE TABLE IF NOT EXISTS users(
                        id IDENTITY PRIMARY KEY,
                        username VARCHAR(60) UNIQUE NOT NULL,
                        password VARCHAR(120) NOT NULL,
                        role VARCHAR(20) NOT NULL
                    )
                    """);
            st.execute("""
                    CREATE TABLE IF NOT EXISTS categories(
                        id IDENTITY PRIMARY KEY,
                        name VARCHAR(100) UNIQUE NOT NULL
                    )
                    """);
            st.execute("""
                    CREATE TABLE IF NOT EXISTS products(
                        id IDENTITY PRIMARY KEY,
                        name VARCHAR(150) NOT NULL,
                        barcode VARCHAR(80) UNIQUE,
                        category_id BIGINT,
                        price DOUBLE NOT NULL,
                        stock_qty INT NOT NULL DEFAULT 0,
                        FOREIGN KEY (category_id) REFERENCES categories(id)
                    )
                    """);
            st.execute("""
                    CREATE TABLE IF NOT EXISTS customers(
                        id IDENTITY PRIMARY KEY,
                        full_name VARCHAR(150) NOT NULL,
                        phone VARCHAR(40),
                        email VARCHAR(120)
                    )
                    """);
            st.execute("""
                    CREATE TABLE IF NOT EXISTS sales(
                        id IDENTITY PRIMARY KEY,
                        customer_id BIGINT,
                        cashier_user_id BIGINT NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        subtotal DOUBLE NOT NULL,
                        discount DOUBLE NOT NULL,
                        tax DOUBLE NOT NULL,
                        total DOUBLE NOT NULL,
                        FOREIGN KEY (customer_id) REFERENCES customers(id),
                        FOREIGN KEY (cashier_user_id) REFERENCES users(id)
                    )
                    """);
            st.execute("""
                    CREATE TABLE IF NOT EXISTS sale_items(
                        id IDENTITY PRIMARY KEY,
                        sale_id BIGINT NOT NULL,
                        product_id BIGINT NOT NULL,
                        quantity INT NOT NULL,
                        unit_price DOUBLE NOT NULL,
                        line_total DOUBLE NOT NULL,
                        FOREIGN KEY (sale_id) REFERENCES sales(id) ON DELETE CASCADE,
                        FOREIGN KEY (product_id) REFERENCES products(id)
                    )
                    """);
            st.execute("""
                    CREATE TABLE IF NOT EXISTS company_settings(
                        setting_key VARCHAR(120) PRIMARY KEY,
                        setting_value VARCHAR(500)
                    )
                    """);
            seedDefaults(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private static void seedDefaults(Connection conn) throws SQLException {
        conn.createStatement().execute("MERGE INTO users(username,password,role) KEY(username) VALUES('admin','admin123','ADMIN')");
        conn.createStatement().execute("MERGE INTO users(username,password,role) KEY(username) VALUES('cashier','cashier123','CASHIER')");
        conn.createStatement().execute("MERGE INTO categories(name) KEY(name) VALUES('General')");
        conn.createStatement().execute("MERGE INTO categories(name) KEY(name) VALUES('Beverages')");
        conn.createStatement().execute("MERGE INTO categories(name) KEY(name) VALUES('Snacks')");
        conn.createStatement().execute("MERGE INTO categories(name) KEY(name) VALUES('Dairy')");
        conn.createStatement().execute("MERGE INTO categories(name) KEY(name) VALUES('Bakery')");
        conn.createStatement().execute("MERGE INTO categories(name) KEY(name) VALUES('Household')");
        conn.createStatement().execute("MERGE INTO categories(name) KEY(name) VALUES('Personal Care')");
        conn.createStatement().execute("MERGE INTO categories(name) KEY(name) VALUES('Staples')");
        conn.createStatement().execute("MERGE INTO categories(name) KEY(name) VALUES('Frozen')");
        conn.createStatement().execute("MERGE INTO categories(name) KEY(name) VALUES('Canned')");
        conn.createStatement().execute("MERGE INTO categories(name) KEY(name) VALUES('Baby Care')");
        conn.createStatement().execute("MERGE INTO categories(name) KEY(name) VALUES('Stationery')");
        conn.createStatement().execute("MERGE INTO company_settings(setting_key,setting_value) KEY(setting_key) VALUES('company.name','Demo POS Store')");
        conn.createStatement().execute("MERGE INTO company_settings(setting_key,setting_value) KEY(setting_key) VALUES('company.address','123 Market Street')");
        conn.createStatement().execute("MERGE INTO company_settings(setting_key,setting_value) KEY(setting_key) VALUES('company.phone','+1-555-0000')");
        conn.createStatement().execute("MERGE INTO company_settings(setting_key,setting_value) KEY(setting_key) VALUES('tax.rate','0.15')");
        conn.createStatement().execute("MERGE INTO company_settings(setting_key,setting_value) KEY(setting_key) VALUES('receipt.footer','Thank you for shopping!')");

        seedDepartmentalProducts(conn);
        seedDemoCustomers(conn);
    }

    private static void seedDepartmentalProducts(Connection conn) throws SQLException {
        String insertSql = """
                MERGE INTO products(name, barcode, category_id, price, stock_qty)
                KEY(barcode)
                VALUES (?, ?, (SELECT id FROM categories WHERE name = ?), ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            addProduct(ps, "Cola 500ml", "DS1001", "Beverages", 120, 120);
            addProduct(ps, "Orange Juice 1L", "DS1002", "Beverages", 340, 80);
            addProduct(ps, "Mineral Water 1.5L", "DS1003", "Beverages", 110, 220);
            addProduct(ps, "Energy Drink 250ml", "DS1004", "Beverages", 280, 75);
            addProduct(ps, "Green Tea Box", "DS1005", "Beverages", 560, 50);
            addProduct(ps, "Mango Drink 1L", "DS1006", "Beverages", 260, 90);
            addProduct(ps, "Lemon Soda 500ml", "DS1007", "Beverages", 140, 130);
            addProduct(ps, "Rooh Afza 800ml", "DS1008", "Beverages", 490, 45);

            addProduct(ps, "Potato Chips 80g", "DS2001", "Snacks", 120, 140);
            addProduct(ps, "Salted Peanuts 150g", "DS2002", "Snacks", 210, 90);
            addProduct(ps, "Chocolate Cookies 200g", "DS2003", "Snacks", 240, 70);
            addProduct(ps, "Nachos 150g", "DS2004", "Snacks", 180, 65);
            addProduct(ps, "Popcorn Butter 90g", "DS2005", "Snacks", 170, 85);
            addProduct(ps, "Crackers 120g", "DS2006", "Snacks", 95, 120);
            addProduct(ps, "Chocolate Wafer 6 Pack", "DS2007", "Snacks", 150, 110);
            addProduct(ps, "Dry Fruit Mix 200g", "DS2008", "Snacks", 680, 40);

            addProduct(ps, "Full Cream Milk 1L", "DS3001", "Dairy", 290, 120);
            addProduct(ps, "Yogurt Plain 500g", "DS3002", "Dairy", 260, 70);
            addProduct(ps, "Cheddar Cheese 200g", "DS3003", "Dairy", 780, 45);
            addProduct(ps, "Butter 200g", "DS3004", "Dairy", 620, 45);
            addProduct(ps, "Eggs 12 Pack", "DS3005", "Dairy", 420, 110);
            addProduct(ps, "Cream 200ml", "DS3006", "Dairy", 190, 80);
            addProduct(ps, "Lassi 1L", "DS3007", "Dairy", 240, 55);
            addProduct(ps, "Mozzarella Cheese 200g", "DS3008", "Dairy", 760, 35);

            addProduct(ps, "White Bread Loaf", "DS4001", "Bakery", 140, 110);
            addProduct(ps, "Brown Bread Loaf", "DS4002", "Bakery", 170, 80);
            addProduct(ps, "Croissant 4 Pack", "DS4003", "Bakery", 420, 55);
            addProduct(ps, "Muffin Chocolate 2 Pack", "DS4004", "Bakery", 260, 60);
            addProduct(ps, "Burger Buns 6 Pack", "DS4005", "Bakery", 210, 75);
            addProduct(ps, "Cake Rusk 500g", "DS4006", "Bakery", 380, 65);
            addProduct(ps, "Tea Cake Small", "DS4007", "Bakery", 480, 35);
            addProduct(ps, "Donut Glazed", "DS4008", "Bakery", 120, 90);

            addProduct(ps, "Dishwash Liquid 500ml", "DS5001", "Household", 350, 80);
            addProduct(ps, "Laundry Powder 1kg", "DS5002", "Household", 760, 55);
            addProduct(ps, "Floor Cleaner 1L", "DS5003", "Household", 620, 50);
            addProduct(ps, "Tissue Box", "DS5004", "Household", 170, 150);
            addProduct(ps, "Aluminum Foil Roll", "DS5005", "Household", 320, 45);
            addProduct(ps, "Toilet Cleaner 500ml", "DS5006", "Household", 390, 70);
            addProduct(ps, "Garbage Bags Large 30pc", "DS5007", "Household", 280, 95);
            addProduct(ps, "Glass Cleaner 500ml", "DS5008", "Household", 340, 60);

            addProduct(ps, "Shampoo 400ml", "DS6001", "Personal Care", 890, 65);
            addProduct(ps, "Soap Bar 125g", "DS6002", "Personal Care", 120, 220);
            addProduct(ps, "Toothpaste 120g", "DS6003", "Personal Care", 260, 130);
            addProduct(ps, "Toothbrush Medium", "DS6004", "Personal Care", 140, 150);
            addProduct(ps, "Hand Wash 250ml", "DS6005", "Personal Care", 290, 90);
            addProduct(ps, "Hair Oil 200ml", "DS6006", "Personal Care", 370, 75);
            addProduct(ps, "Face Wash 100ml", "DS6007", "Personal Care", 520, 55);
            addProduct(ps, "Shaving Foam 200ml", "DS6008", "Personal Care", 540, 45);

            addProduct(ps, "Basmati Rice 5kg", "DS7001", "Staples", 3200, 50);
            addProduct(ps, "Wheat Flour 5kg", "DS7002", "Staples", 1450, 70);
            addProduct(ps, "Cooking Oil 1L", "DS7003", "Staples", 680, 130);
            addProduct(ps, "Sugar 1kg", "DS7004", "Staples", 180, 180);
            addProduct(ps, "Salt Iodized 800g", "DS7005", "Staples", 95, 170);
            addProduct(ps, "Daal Chana 1kg", "DS7006", "Staples", 420, 95);
            addProduct(ps, "Daal Masoor 1kg", "DS7007", "Staples", 390, 100);
            addProduct(ps, "Tea Premium 475g", "DS7008", "Staples", 1190, 60);

            addProduct(ps, "Chicken Nuggets 750g", "DS8001", "Frozen", 980, 70);
            addProduct(ps, "Paratha Family Pack", "DS8002", "Frozen", 560, 85);
            addProduct(ps, "French Fries 1kg", "DS8003", "Frozen", 740, 65);
            addProduct(ps, "Frozen Peas 500g", "DS8004", "Frozen", 320, 60);
            addProduct(ps, "Ice Cream Vanilla 1L", "DS8005", "Frozen", 690, 50);
            addProduct(ps, "Frozen Samosa 20pc", "DS8006", "Frozen", 610, 45);

            addProduct(ps, "Baked Beans 400g", "DS9001", "Canned", 380, 55);
            addProduct(ps, "Tuna Can 170g", "DS9002", "Canned", 520, 45);
            addProduct(ps, "Tomato Paste 400g", "DS9003", "Canned", 260, 70);
            addProduct(ps, "Sweet Corn 400g", "DS9004", "Canned", 290, 65);
            addProduct(ps, "Mushroom Can 400g", "DS9005", "Canned", 420, 40);
            addProduct(ps, "Mixed Fruit Can 825g", "DS9006", "Canned", 650, 35);

            addProduct(ps, "Baby Diapers Small 34", "DSA001", "Baby Care", 1650, 45);
            addProduct(ps, "Baby Diapers Medium 30", "DSA002", "Baby Care", 1720, 50);
            addProduct(ps, "Baby Wipes 72 Sheets", "DSA003", "Baby Care", 390, 110);
            addProduct(ps, "Baby Lotion 200ml", "DSA004", "Baby Care", 540, 60);
            addProduct(ps, "Baby Shampoo 200ml", "DSA005", "Baby Care", 520, 55);
            addProduct(ps, "Baby Powder 200g", "DSA006", "Baby Care", 360, 70);

            addProduct(ps, "Notebook A4 100 Pages", "DSB001", "Stationery", 180, 130);
            addProduct(ps, "Ball Pen Blue 10 Pack", "DSB002", "Stationery", 220, 120);
            addProduct(ps, "Permanent Marker", "DSB003", "Stationery", 95, 90);
            addProduct(ps, "Stapler Medium", "DSB004", "Stationery", 260, 50);
            addProduct(ps, "Glue Stick 35g", "DSB005", "Stationery", 110, 100);
            addProduct(ps, "Printer Paper A4 500 Sheets", "DSB006", "Stationery", 1450, 35);
        }
    }

    private static void addProduct(PreparedStatement ps, String name, String barcode, String category, double price, int stockQty) throws SQLException {
        ps.setString(1, name);
        ps.setString(2, barcode);
        ps.setString(3, category);
        ps.setDouble(4, price);
        ps.setInt(5, stockQty);
        ps.executeUpdate();
    }

    private static void seedDemoCustomers(Connection conn) throws SQLException {
        conn.createStatement().execute("MERGE INTO customers(full_name, phone, email) KEY(full_name) VALUES('Walk-in Customer', '+0000000000', 'walkin@example.com')");
        conn.createStatement().execute("MERGE INTO customers(full_name, phone, email) KEY(full_name) VALUES('Ali Raza', '+923001112233', 'ali.raza@example.com')");
        conn.createStatement().execute("MERGE INTO customers(full_name, phone, email) KEY(full_name) VALUES('Ayesha Khan', '+923221234567', 'ayesha.khan@example.com')");
    }
}
