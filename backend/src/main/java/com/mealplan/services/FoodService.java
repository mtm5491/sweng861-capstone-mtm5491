package com.mealplan.services;
import com.mealplan.dao.FoodDao;
import com.mealplan.models.Food;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Statement;

/**
 * Handles CRUD Operations for food model
 * Create, retrieve, edit, and delete foods
 */
public class FoodService {
    List<Food> foodArray = new ArrayList<>();
    private final FoodDao dao;

    // Constructor for test cases
    public FoodService(FoodDao dao) {
        this.dao = dao;
    }
    /**
     * Insert new food into database
     * @param food to insert into database
     * @return food inserted
     */
    public Food createFood(Food food) {
        System.out.println("Creating food in FoodService");
        // Make sure there is a description, ownerID, and it doesn't already exist

        if (food.getDescription() == null || food.getDescription().isBlank()) {
            System.out.println("DESCRIPTION NULL");
            throw new IllegalArgumentException("Description is Required");
        }
        if (food.getOwnerID() == null || food.getOwnerID().isBlank()) {
            System.out.println("OWNER ID NULL");
            throw new IllegalArgumentException("Owner ID is required");
        }
        // check for duplicates
        if (dao.existsByDescription(food.getOwnerID(), food.getDescription())) {
            System.out.println("ALREADY EXISTS");
            throw new IllegalArgumentException("A food with this description already exists");
        }
        System.out.println("Creating food --> Going to DAO");
        return dao.insert(food);
    }

    /**
     * Lists all food by ownerID
     * @param ownerID ownerID of food retrieved
     * @return List of food items
     */
    public List<Food> getAllFoods(String ownerID) {
        return dao.findByOwner(ownerID);
    }

    /**
     * Retrieve food by ID
     * @param id of food to retrieve
     * @return Food
     */
    public Optional<Food> getFoodById(String owner_id, int id) {
        System.out.println("IN SERVICE: getting food by ID: " + id);
        // returned all food that matches that id
        Optional<Food> food = dao.findById(id, owner_id);
        System.out.println("Returning food by ID");
        return food;
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
    public boolean updateFood(int id, Food updatedFood, String ownerId) {
        // get existing food
        Optional <Food> existing = dao.findById(id, ownerId);
        if (existing.isEmpty()) {
            return false;
        }
        Food current = existing.get();
        // make sure it belongs to the owner
        if (!current.getOwnerID().equals(ownerId)) {
            return false;
        }
        current.setDescription(updatedFood.getDescription());
        current.setFdcId(updatedFood.getFdcId());
        current.setFoodNutrients(updatedFood.getFoodNutrients());
        // update SQL
        return dao.update(updatedFood);
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



