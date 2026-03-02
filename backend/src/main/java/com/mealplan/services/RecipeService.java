package com.mealplan.services;

import com.mealplan.models.Recipe;
import com.mealplan.dao.RecipeDao;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecipeService {
    List<Recipe> recipeArray = new ArrayList<>();
    private final RecipeDao dao;

    // Constructor for test cases
    public RecipeService(RecipeDao dao) {
        this.dao = dao;
        System.out.println("Recipe Service created...");
    }
    /**
     * Insert new food into database
     * @param food to insert into database
     * @return food inserted
     */
    public Recipe createRecipe(Recipe recipe) {
        System.out.println("Creating recipe in RecipeService");
    
        if (recipe.getDescription() == null || recipe.getDescription().isBlank()) {
            System.out.println("DESCRIPTION NULL");
            throw new IllegalArgumentException("Description is Required");
        }
        if (recipe.getOwnerId() == null || recipe.getOwnerId().isBlank()) {
            System.out.println("OWNER ID NULL");
            throw new IllegalArgumentException("Owner ID is required");
        }
  

        if (recipe.getInstructions() == null || recipe.getInstructions().isBlank()) {
            System.out.println("INSTRUCTIONS NULL");
            throw new IllegalArgumentException("Instructions are required");
        }

        if (recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            System.out.println("INGREDIENTS NULL");
            throw new IllegalArgumentException("Ingredients are required");
        }

        if (dao.existsByName(recipe.getOwnerId(), recipe.getName())) {
            System.out.println("ALREADY EXISTS");
            throw new IllegalArgumentException("A recipe with this description already exists");
        }

        System.out.println("Creating recipe --> Going to DAO");
        return dao.insert(recipe);
    }

    /**
     * Lists all food by ownerID
     * @param ownerID ownerID of food retrieved
     * @return List of food items
     */
    public List<Recipe> getAllRecipes(String ownerID) {
        return dao.findByOwner(ownerID);
    }

    /**
     * Retrieve food by ID
     * @param id of food to retrieve
     * @return Food
     */
    public Optional<Recipe> getRecipeById(String owner_id, int id) {
        System.out.println("IN SERVICE: getting food by ID: " + id);
        // returned all food that matches that id
        Optional<Recipe> recipe = dao.findById(id, owner_id);
        System.out.println("Returning food by ID");
        return recipe;
        // filter for only the food that belongs to that owner
        // return food.filter(f -> f.getOwnerID().equals(owner_id));
    }

    /**
     * Update food
     * @param id of food to update
     * @param updatedFood updated food
     * @param ownerId owner ID
     * @return true if updated, false otherwise
     */
    public boolean updateRecipe(int id, Recipe updatedRecipe, String ownerId) {
        // get existing food
        Optional <Recipe> existing = dao.findById(id, ownerId);
        if (existing.isEmpty()) {
            return false;
        }
        Recipe current = existing.get();
        // make sure it belongs to the owner
        if (!current.getOwnerId().equals(ownerId)) {
            return false;
        }
        current.setName(updatedRecipe.getName());
        current.setDescription(updatedRecipe.getDescription());
        current.setInstructions(updatedRecipe.getInstructions());
        current.setIngredients(updatedRecipe.getIngredients());
        // update SQL
        return dao.update(updatedRecipe);
    }

    /**
     * Delete food
     * @param id of food to delete
     * @param ownerID owner ID
     * @return true if update, false otherwise
     */
    public boolean deleteFood(int id, String ownerID) {
        return dao.delete(id, ownerID);
    }
}
