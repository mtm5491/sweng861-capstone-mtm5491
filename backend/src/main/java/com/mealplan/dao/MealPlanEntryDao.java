package com.mealplan.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import com.mealplan.models.MealPlanEntry;

import java.sql.*;

public class MealPlanEntryDao {

private final DataSource dataSource;
    
    public MealPlanEntryDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public MealPlanEntry insert(MealPlanEntry mealPlanEntry) {
        String sql = "INSERT INTO public.meal_plan_entries (meal_plan_id, recipe_id, date, meal_type) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {           
            stmt.setInt(1, mealPlanEntry.getMealPlanId());
            stmt.setInt(2, mealPlanEntry.getRecipeId());
            stmt.setDate(3, Date.valueOf(mealPlanEntry.getDate()));
            stmt.setString(4, mealPlanEntry.getMealType());
            stmt.executeUpdate();  
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                mealPlanEntry.setId(rs.getInt(1));
            }   
            return mealPlanEntry;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

   
    public Optional<MealPlanEntry> findById(int id) {
       String sql = "SELECT id, meal_plan_id, recipe_id, date, meal_type FROM public.meal_plan_entries WHERE id = ?";
       try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                MealPlanEntry mealPlanEntry = new MealPlanEntry();
                mealPlanEntry.setId(rs.getInt("id"));
                mealPlanEntry.setMealPlanId(rs.getInt("meal_plan_id"));
                mealPlanEntry.setRecipeId(rs.getInt("recipe_id"));
                mealPlanEntry.setDate(rs.getDate("date").toString());
                mealPlanEntry.setMealType(rs.getString("meal_type"));
                return Optional.of(mealPlanEntry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    } 
    


    public boolean update(MealPlanEntry mealPlanEntry) {
        String sql = "UPDATE public.meal_plan_entries SET meal_plan_id = ?, recipe_id = ?, date = ?, meal_type = ? " +
                "WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mealPlanEntry.getMealPlanId());
            stmt.setInt(2, mealPlanEntry.getRecipeId());
            stmt.setDate(3, Date.valueOf(mealPlanEntry.getDate()));
            stmt.setString(4, mealPlanEntry.getMealType());
            stmt.setInt(5, mealPlanEntry.getId());
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM public.meal_plan_entries WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting mealPlanEntry", e);
        }
    }

    public void clearMealPlanEntryTable() {
        String sql = "TRUNCATE TABLE public.meal_plan_entries RESTART IDENTITY;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }   

    public List<MealPlanEntry> getByMealPlanId(int mealPlanId) {
    List<MealPlanEntry> entries = new ArrayList<>();

    String sql = """
        SELECT id, meal_plan_id, recipe_id, date, meal_type FROM public.meal_plan_entries WHERE meal_plan_id = ?
        ORDER BY date, meal_type
    """;

    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, mealPlanId);

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                MealPlanEntry entry = new MealPlanEntry();
                entry.setId(rs.getInt("id"));
                entry.setMealPlanId(rs.getInt("meal_plan_id"));
                entry.setRecipeId(rs.getInt("recipe_id"));
                entry.setDate(rs.getString("date"));
                entry.setMealType(rs.getString("meal_type"));

                entries.add(entry);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return entries;
}

}
