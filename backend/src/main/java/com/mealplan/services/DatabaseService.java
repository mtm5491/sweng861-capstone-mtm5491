package com.mealplan.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Manages all JDBC interactions with PostgreSQL
 * - Loads PostgreSQL JDBC Driver, provides getConnection() helper, offers methods for inserting
 * USDA food data
 */
public class DatabaseService {
    /** JDBC Connection URL */
    private static final String URL = System.getenv("DB_URL");
    /** Database Username */
    private static final String USER = System.getenv("DB_USER");
    /** Database Password */
    private static final String PASS = System.getenv("DB_PASSWORD");

    /**
     * load PostgreSQL driver when class is loaded
     */
    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL JDBC Driver registered");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * Save food item to the database
     * @param fdcID USDA ID
     * @param description Food Description
     * @param nutrientData Nutrient info JSON
     */
    public static void saveFood(int fdcID, String description, String nutrientData) {
        String sql = "INSERT INTO public.food (fdc_id, description, nutrients) VALUES (?, ?, ?::jsonb)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, fdcID);
            stmt.setString(2, description);
            stmt.setString(3, nutrientData);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Utility method to get a database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
