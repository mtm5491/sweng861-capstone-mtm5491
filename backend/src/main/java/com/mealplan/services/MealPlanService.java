package com.mealplan.services;


import com.mealplan.models.MealPlan;
import com.mealplan.models.MealPlanEntry;
import com.mealplan.dao.MealPlanDao;
import com.mealplan.dao.MealPlanEntryDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator.OfPrimitive;

public class MealPlanService {
    List<MealPlan> mealPlanArray = new ArrayList<>();
    private final MealPlanDao dao;
    private final MealPlanEntryDao mealPlanEntryDao;

    // Constructor for test cases
    public MealPlanService(MealPlanDao dao, MealPlanEntryDao mealPlanEntryDao) {
        this.dao = dao;
        this.mealPlanEntryDao = mealPlanEntryDao;
        System.out.println("Meal Plan Service created...");
    }
    /**
     * Insert new food into database
     * @param food to insert into database
     * @return food inserted
     */
    public MealPlan createMealPlan(MealPlan mealPlan) {
        System.out.println("Creating mealPlan in MealPlanService");
    
        if (mealPlan.getName() == null || mealPlan.getName().isBlank()) {
            System.out.println("NAME NULL");
            throw new IllegalArgumentException("Name is Required");
        }
        if (mealPlan.getOwnerId() == null || mealPlan.getOwnerId().isBlank()) {
            System.out.println("OWNER ID NULL");
            throw new IllegalArgumentException("Owner ID is required");
        }
        if (mealPlan.getStartDate() == null) {
            System.out.println("START DATE NULL");
            throw new IllegalArgumentException("Start Date is required");
        }
        if (mealPlan.getEndDate() == null) {
            System.out.println("END DATE NULL");
            throw new IllegalArgumentException("End Date is required");
        }

        if (dao.existsByName(mealPlan.getOwnerId(), mealPlan.getName())) {
            System.out.println("ALREADY EXISTS");
            throw new IllegalArgumentException("A mealPlan with this name already exists");
        }

        System.out.println("Creating mealPlan --> Going to DAO");
        return dao.insert(mealPlan);
    }

    /**
     * Lists all food by ownerID
     * @param ownerID ownerID of food retrieved
     * @return List of food items
     */
    public List<MealPlan> getAllMealPlans(String ownerID) {
        return dao.findByOwner(ownerID);
    }

    /**
     * Retrieve food by ID
     * @param id of food to retrieve
     * @return Food
     */
    public Optional<MealPlan> getMealPlanById(String owner_id, int id) {
        System.out.println("IN SERVICE: getting food by ID: " + id);
        // returned all food that matches that id
        Optional<MealPlan> mealPlan = dao.findById(id, owner_id);
        if (mealPlan.isEmpty()) {
            return Optional.empty();
        }

        MealPlan plan = mealPlan.get();
        
        List<MealPlanEntry> entries = mealPlanEntryDao.getByMealPlanId(id);
        plan.setEntries(entries);

        System.out.println("Returning mealPlan by ID");
        return Optional.of(plan);
    }

    /**
     * Update food
     * @param id of food to update
     * @param updatedFood updated food
     * @param ownerId owner ID
     * @return true if updated, false otherwise
     */
    public boolean updateMealPlan(int id, MealPlan updatedMealPlan, String ownerId) {
        // get existing food
        Optional <MealPlan> existing = dao.findById(id, ownerId);
        if (existing.isEmpty()) {
            return false;
        }
        MealPlan current = existing.get();
        // make sure it belongs to the owner
        if (!current.getOwnerId().equals(ownerId)) {
            return false;
        }
        current.setName(updatedMealPlan.getName());
        current.setStartDate(updatedMealPlan.getStartDate());
        current.setEndDate(updatedMealPlan.getEndDate());
        // update SQL
        return dao.update(updatedMealPlan);
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
