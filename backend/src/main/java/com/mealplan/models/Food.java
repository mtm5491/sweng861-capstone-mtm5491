package com.mealplan.models;

/**
 * Holds information related to food items
 */
public class Food {
    private String owner_id;
    private int id;
    private int fdcId;
    private String description;
    private String foodNutrients; // JSON string representing nutrients

    // NEW
    // private String unit;
    // private double quantity;
    // private double calories;
    // private double fat;
    // private double carbs;
    // private double protein;

    /**
     * Empty constructor
     */
    public Food() {
    }

    /**
     * Full constructor
     * @param id food id
     * @param description food desription
     * @param foodNutrients nutrients
     * @param owner_id owner ID
     */
    public Food(int id, String description, String foodNutrients, String owner_id) {
        this.id = id;
        this.description = description;
        this.foodNutrients = foodNutrients;
        this.owner_id = owner_id;

        // Process description 
        processFoodNutrients(foodNutrients);
    }

    /**
     * Get food id
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set food id
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Return FDC Id
     * @return FDC ID
     */
    public int getFdcId() {
        return fdcId;
    }

    /**
     * Set FCD Id
     */
    public void setFdcId(int fdcId) {
        this.fdcId = fdcId;
    }

    /**
     * Get Description
     * @return Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set Description
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get Food Nutrients
     * @return Food Nutrients
     */
    public String getFoodNutrients() {
        return foodNutrients;
    }

    /**
     * Set Food Nutrients
     * @param foodNutrients
     */
    public void setFoodNutrients(String foodNutrients) {
        this.foodNutrients = foodNutrients;
    }

    /**
     * Get Owner ID
     * @return Owner ID
     */
    public String getOwnerID() {
        return owner_id;
    }

    /**
     * Set Owner ID
     * @param owner_id
     */
    public void setOwnerID(String owner_id) {
        this.owner_id = owner_id;
    }

    @Override
    public String toString() {
        return "Food{" +
            "id=" + id +
            ", fdcId=" + fdcId +
            ", description='" + description + '\'' +
            ", foodNutrients='" + foodNutrients + '\'' +
            ", owner_id='" + owner_id + '\'' +
            '}';
    }

    private void processFoodNutrients(String foodNutrients) {
        

    }

}
