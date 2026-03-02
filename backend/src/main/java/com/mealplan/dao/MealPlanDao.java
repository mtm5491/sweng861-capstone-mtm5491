package com.mealplan.dao;

import javax.sql.DataSource;
import java.sql.Date;
import java.time.LocalDate;

import com.mealplan.models.MealPlan;
import java.sql.*;
import java.util.*;



public class MealPlanDao {

private final DataSource dataSource;
    
    public MealPlanDao(DataSource dataSource) {
        this.dataSource = dataSource;
        System.out.println("Meal Plan DAO created...");
    }
    
    public boolean existsByName(String ownerId, String name) {
        String sql = "SELECT 1 FROM public.meal_plans WHERE owner_id = ? AND LOWER(name) = LOWER(?) LIMIT 1;";
        try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ownerId);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking duplicate description", e);
        }
    }
    
    public MealPlan insert(MealPlan mealPlan) {
        String sql = "INSERT INTO public.meal_plans (owner_id, name, start_date, end_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    
            stmt.setString(1, mealPlan.getOwnerId());
            stmt.setString(2, mealPlan.getName());
            stmt.setDate(3, Date.valueOf(mealPlan.getStartDate()));
            stmt.setDate(4, Date.valueOf(mealPlan.getEndDate()));
            stmt.executeUpdate();  
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                mealPlan.setId(rs.getInt(1));
            }   
            return mealPlan;
        } catch (Exception e) {
            throw new RuntimeException("Error inserting meal plan", e);
        }
    }

    // @Override
    public List<MealPlan> findByOwner(String ownerId) {
        List<MealPlan> mealPlans = new ArrayList<>();
        String sql = "SELECT id, owner_id, name, start_date, end_date FROM public.meal_plans WHERE owner_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1,ownerId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    MealPlan mealPlan = new MealPlan();
                    mealPlan.setId(rs.getInt("id"));
                    mealPlan.setOwnerId(rs.getString("owner_id"));
                    mealPlan.setName(rs.getString("name"));
                    mealPlan.setStartDate(rs.getDate("start_date").toString());
                    mealPlan.setEndDate(rs.getDate("end_date").toString());
                    
                    mealPlans.add(mealPlan);
                }
        } catch (Exception e) {
            throw new RuntimeException("Error finding meal plan by owner", e);
        }
        return mealPlans;
    }

   
    public Optional<MealPlan> findById(int id, String owner_id) {
       String sql = "SELECT id, owner_id, name, start_date, end_date FROM public.meal_plans WHERE id = ? AND owner_id = ?";
       try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, owner_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                MealPlan mealPlan = new MealPlan();
                mealPlan.setId(rs.getInt("id"));
                mealPlan.setOwnerId(rs.getString("owner_id"));
                mealPlan.setName(rs.getString("name"));
                mealPlan.setStartDate(rs.getDate("start_date").toString());
                mealPlan.setEndDate(rs.getDate("end_date").toString());

                return Optional.of(mealPlan);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error finding meal plan by Id", e);
        }
       return null;
    } 
    


    public boolean update(MealPlan mealPlan) {
        String sql = "UPDATE public.meal_plans SET name = ?, start_date = ?, end_date = ? " +
                "WHERE id = ? AND owner_id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mealPlan.getName());
            stmt.setDate(2, Date.valueOf(mealPlan.getStartDate()));
            stmt.setDate(3, Date.valueOf(mealPlan.getEndDate()));
            stmt.setInt(4, mealPlan.getId());
            stmt.setString(5, mealPlan.getOwnerId());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error updating meal plan", e);
        }
    }

    public boolean delete(int id, String ownerId) {
        String sql = "DELETE FROM public.meal_plans WHERE id = ? AND owner_id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, ownerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting food", e);
        }
    }

    public void clearMealPlanTable() {
        String sql = "TRUNCATE TABLE public.meal_plans CASCADE;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }   
}
