package com.mealplan.dao;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import com.mealplan.Logger;
import com.mealplan.models.Recipe;
import com.mealplan.models.RecipeIngredient;

public class RecipeDao {

private final DataSource dataSource;
    
    public RecipeDao(DataSource dataSource) {
        this.dataSource = dataSource;
        System.out.println("Recipe DAO created...");
    }
    
    public boolean existsByName(String ownerId, String name) {
        String sql = "SELECT 1 FROM public.recipes WHERE owner_id = ? AND LOWER(name) = LOWER(?) LIMIT 1;";
        try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ownerId);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking duplicate name", e);
        }
    }
    
    public Recipe insert(Recipe recipe) {
        System.out.println("Inserting recipe in DAO");
        String sql = "INSERT INTO public.recipes (owner_id, name, description, instructions) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            System.out.println("Creating prepared statement");
            stmt.setString(1, recipe.getOwnerId());
            stmt.setString(2, recipe.getName());
            stmt.setString(3, recipe.getDescription());
            stmt.setString(4, recipe.getInstructions());
            stmt.executeUpdate();  
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                System.out.println("Setting recipe ID");
                recipe.setId(rs.getInt(1));
            }   
            System.out.println("Adding ingredients...");
            insertIngredients(recipe.getId(), recipe.getIngredients());
            System.out.println("Ingredients added");    
            return recipe;
         } catch (SQLException e) {
            throw new RuntimeException("Error inserting recipe", e);
        }
    }

    // @Override
    public List<Recipe> findByOwner(String ownerId) {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT id, owner_id, name, description, instructions FROM public.recipes WHERE owner_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1,ownerId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Recipe recipe = new Recipe();
                    recipe.setId(rs.getInt("id"));
                    recipe.setOwnerId(rs.getString("owner_id"));
                    recipe.setName(rs.getString("name"));
                    recipe.setDescription(rs.getString("description"));
                    recipe.setInstructions(rs.getString("instructions"));
                    recipe.setIngredients(findIngredients(recipe.getId()));
                    recipes.add(recipe);
                    
                }
         } catch (SQLException e) {
            throw new RuntimeException("Error finding recipe by owner", e);
        }
        return recipes;
    }

   
    public Optional<Recipe> findById(int id, String owner_id) {
       String sql = "SELECT id, owner_id, name, description, instructions FROM public.recipes WHERE id = ? AND owner_id = ?";
       try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, owner_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Recipe recipe = new Recipe();
                recipe.setId(rs.getInt("id"));
                recipe.setOwnerId(rs.getString("owner_id"));
                recipe.setName(rs.getString("name"));
                recipe.setDescription(rs.getString("description"));
                recipe.setInstructions(rs.getString("instructions"));
                recipe.setIngredients(findIngredients(recipe.getId()));

                return Optional.of(recipe);
            }
         } catch (SQLException e) {
            throw new RuntimeException("Error finding recipe by Id", e);
        }
        return Optional.empty();
    } 
    


    public boolean update(Recipe recipe) {
        String sql = "UPDATE public.recipes SET name = ?, description = ?, instructions = ? " +
                "WHERE id = ? AND owner_id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, recipe.getName());
            stmt.setString(2, recipe.getDescription());
            stmt.setString(3, recipe.getInstructions());
            stmt.setInt(4, recipe.getId());
            stmt.setString(5, recipe.getOwnerId());
            deleteIngredients(recipe.getId());
            insertIngredients(recipe.getId(), recipe.getIngredients());


            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating recipe", e);
        }
    }

    public boolean delete(int id, String ownerId) {
        String sql = "DELETE FROM public.recipes WHERE id = ? AND owner_id = ?";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setString(2, ownerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting recipe", e);
        }
    }

    public void clearRecipeTable() {
        String sql = "TRUNCATE TABLE public.recipes CASCADE;";
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // sql = "TRUNCATE TABLE public.recipe_ingredients RESTART IDENTITY;";
        // try (Connection conn = dataSource.getConnection();
        //     PreparedStatement stmt = conn.prepareStatement(sql)) {
        //     stmt.executeUpdate();
        // } catch (SQLException e) {
        //     throw new RuntimeException(e);
        // }

    }   

    public void insertIngredients(int recipeId, List<RecipeIngredient> ingredients) {

        String sql = "INSERT INTO public.recipe_ingredients (recipe_id, ingredient_name, quantity, unit) VALUES (?, ?, ?, ?)";
        System.out.println("Inserting ingredients");  
        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            System.out.println("Creating prepared statement");  
            for (RecipeIngredient ing : ingredients) {
                stmt.setInt(1, recipeId);
                stmt.setString(2, ing.getName());
                stmt.setString(3, ing.getQuantity());
                stmt.setString(4, ing.getUnit());
                stmt.addBatch();
            }
            System.out.println("Executing statement for " + stmt.toString());  
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteIngredients(int recipeId) {
        String sql = "DELETE FROM public.recipe_ingredients WHERE recipe_id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recipeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public List<RecipeIngredient> findIngredients(int recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        String sql = "SELECT id, recipe_id, ingredient_name, quantity, unit FROM recipe_ingredients WHERE recipe_id = ?";

        try (Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, recipeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RecipeIngredient ing = new RecipeIngredient();
                ing.setId(rs.getInt("id"));
                ing.setRecipeId(rs.getInt("recipe_id"));
                ing.setName(rs.getString("ingredient_name"));
                ing.setQuantity(rs.getString("quantity"));
                ing.setUnit(rs.getString("unit"));
                list.add(ing);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;

            
        }

}
