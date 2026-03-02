package com.mealplan.dao;
import com.mealplan.models.Food;
import com.mealplan.services.DatabaseService;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class JdbcFoodDao implements FoodDao {
    private final DataSource dataSource;
    
    public JdbcFoodDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public boolean existsByDescription(String ownerId, String description) {
        String sql = "SELECT 1 FROM public.food WHERE owner_id = ? AND LOWER(description) = LOWER(?) LIMIT 1;";
        try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ownerId);
            stmt.setString(2, description);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking duplicate description", e);
        }

    }
    
    @Override
    public Food insert(Food food) {
        String sql = "INSERT INTO public.food (description, fdc_id, nutrients, owner_id) VALUES (?, ?, ?::jsonb, ?)";
        System.out.println("Inserting food:");
        System.out.println("User: " + food.getOwnerID());
        System.out.println("Description: " + food.getDescription());

        System.out.println("FDCID: " + food.getFdcId());
        System.out.println("Food Nutrients: " + food.getFoodNutrients());
        try (Connection conn = DatabaseService.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            
            stmt.setString(1, food.getDescription());
            stmt.setInt(2, food.getFdcId());
            stmt.setString(3, food.getFoodNutrients());
            stmt.setString(4, food.getOwnerID());
            stmt.executeUpdate();  
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                food.setId(rs.getInt(1));
            }   
            return food;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Food> findByOwner(String ownerId) {
        List<Food> foods = new ArrayList<>();
        String sql = "SELECT id, description, fdc_id, nutrients, owner_id FROM public.food WHERE owner_id = ?";
        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1,ownerId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Food food = new Food();
                    food.setId(rs.getInt("id"));
                    food.setDescription(rs.getString("description"));
                    food.setFdcId(rs.getInt("fdc_id"));
                    food.setFoodNutrients(rs.getString("nutrients"));
                    food.setOwnerID(rs.getString("owner_id"));
                    foods.add(food);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return foods;
    }

    @Override
    public Optional<Food> findById(int id, String owner_id) {
       System.out.println("Find by ID:" + id);
       String sql = "SELECT id, fdc_id, description, nutrients, owner_id FROM public.food WHERE id = ? AND owner_id = ?";
       try (Connection conn = DatabaseService.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, owner_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Food food = new Food();
                food.setId(rs.getInt("id"));
                food.setFdcId(rs.getInt("fdc_id"));
                food.setDescription(rs.getString("description"));
                System.out.print("Description:" + food.getDescription());
                food.setFoodNutrients(rs.getString("nutrients"));
                food.setOwnerID(rs.getString("owner_id"));
                System.out.print("Owner:" + food.getOwnerID());
                return Optional.of(food);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    } 
    

    @Override
    public boolean update(Food food) {
        String sql = "UPDATE public.food SET fdc_id = ?, description = ?, nutrients = ?::jsonb " +
                "WHERE id = ? AND owner_id = ?";
        try (Connection conn = DatabaseService.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, food.getFdcId());
            stmt.setString(2, food.getDescription());
            stmt.setString(3, food.getFoodNutrients());
            stmt.setInt(4, food.getId());
            stmt.setString(5, food.getOwnerID());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id, String ownerId) {
        String sql = "DELETE FROM public.food WHERE id = ? AND owner_id = ?";
        try (Connection conn = DatabaseService.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, ownerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting food", e);
        }
    }
    @Override
    public void clearFoodsTable() {
        String sql = "TRUNCATE TABLE food RESTART IDENTITY;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }   

}

        

