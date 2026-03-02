package com.mealplan.models;

import java.time.LocalDate;

public class MealPlanEntry {
    
    private int id;
    private String ownerId;
    private int mealPlanId;
    private int recipeId;
    private String date;
    private String mealType; //breakfast, lunch, dinner, snack


     public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getMealPlanId() {
        return mealPlanId;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public void setMealPlanId(int mealPlanId) {
        this.mealPlanId = mealPlanId;
    }
    public int getRecipeId() {
        return recipeId;
    }
    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getMealType() {
        return mealType;
    }
    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
    
}
