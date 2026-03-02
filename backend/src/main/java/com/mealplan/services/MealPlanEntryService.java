package com.mealplan.services;


import com.mealplan.models.MealPlan;
import com.mealplan.models.MealPlanEntry;
import com.mealplan.auth.User;
import com.mealplan.dao.MealPlanDao;
import com.mealplan.dao.MealPlanEntryDao;
import com.mealplan.dao.RecipeDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class MealPlanEntryService {
    List<MealPlanEntry> mealPlanEntryArray = new ArrayList<>();
    private final MealPlanEntryDao dao;
    private final MealPlanDao mealPlanDao;
    private final RecipeDao recipeDao;

    // Constructor for test cases
    public MealPlanEntryService(MealPlanEntryDao dao, MealPlanDao mealPlanDao, RecipeDao recipeDao) {
        this.dao = dao;
        this.mealPlanDao = mealPlanDao;
        this.recipeDao = recipeDao;
    }
    /**
     * Insert new mealPlanEntry into database
     * @param mealPlanEntry to insert into database
     * @return mealPlanEntry inserted
     */
    public MealPlanEntry createMealPlanEntry(MealPlanEntry mealPlanEntry, String ownerId) {
        System.out.println("Creating mealPlanEntry in MealPlanEntryService");

        if (mealPlanEntry.getMealPlanId() <= 0) {
            throw new IllegalArgumentException("mealPlanId is required");
        }

        if (mealPlanEntry.getRecipeId() <= 0) {
            throw new IllegalArgumentException("recipeId is required");
        }

        if (mealPlanEntry.getDate() == null) {
            throw new IllegalArgumentException("Date is required");
        }

        if (mealPlanEntry.getMealType() == null || mealPlanEntry.getMealType().isBlank()) {
            throw new IllegalArgumentException("Meal type is required");
        }
        // verify User
        verifyUser(mealPlanEntry, ownerId);
        System.out.println("User Verified");
        // Optional<MealPlan> plan = mealPlanDao.findById(mealPlanEntry.getMealPlanId(), ownerId);
        // if (!plan.isEmpty()) {
        //     plan.get().addMealPlanEntry(mealPlanEntry);
        // }
        return dao.insert(mealPlanEntry);
        
    }

    /**
     * Retrieve mealPlanEntry by ID
     * @param id of mealPlanEntry to retrieve
     * @return Food
     */
    public Optional<MealPlanEntry> getMealPlanEntryById(int id, String ownerId) {
        Optional<MealPlanEntry> mealPlanEntry = dao.findById(id);
        if (mealPlanEntry.isEmpty()) {
            return Optional.empty();
        }
        verifyUser(mealPlanEntry.get(), ownerId);
         return mealPlanEntry;
    }

    /**
     * Update mealPlanEntry
     * @param id of mealPlanEntry to update
     * @param updatedFood updated mealPlanEntry
     * @param ownerId owner ID
     * @return true if updated, false otherwise
     */
    public boolean updateMealPlanEntry(int id, MealPlanEntry updatedMealPlanEntry, String ownerId) {
        // get existing mealPlanEntry
        Optional <MealPlanEntry> existing = dao.findById(id);
        if (existing.isEmpty()) {
            return false;
        }
        // verify user
        MealPlanEntry current = existing.get();
        verifyUser(current, ownerId);
       
        // Update
        current.setMealPlanId(updatedMealPlanEntry.getMealPlanId());
        current.setRecipeId(updatedMealPlanEntry.getRecipeId());
        current.setDate(updatedMealPlanEntry.getDate());
        current.setMealType(updatedMealPlanEntry.getMealType());

        // Ownership check on new mealPlanId/recipeId
        verifyUser(current, ownerId);
        return dao.update(current);
    }

    /**
     * Delete mealPlanEntry
     * @param id of mealPlanEntry to delete
     * @param ownerID owner ID
     * @return true if update, false otherwise
     */
    public boolean deleteMealPlanEntry(int id, String ownerId) {
        Optional<MealPlanEntry> mealPlanEntry = dao.findById(id);
        if (mealPlanEntry.isEmpty()) {
            return false;
        }
        verifyUser(mealPlanEntry.get(), ownerId);
        return dao.delete(id);
    }

    public void verifyUser(MealPlanEntry entry, String ownerId) {
        System.out.println("Verifying User");
            // 1. Validate meal plan belongs to user
        if (mealPlanDao.findById(entry.getMealPlanId(), ownerId).isEmpty()) {
            throw new IllegalStateException("Not your meal plan");
        }

        // 2. Validate recipe belongs to user
        if (recipeDao.findById(entry.getRecipeId(), ownerId).isEmpty()) {
            throw new IllegalStateException("Not your recipe");
        }
    }
}
