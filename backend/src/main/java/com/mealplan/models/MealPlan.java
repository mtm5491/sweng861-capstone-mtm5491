package com.mealplan.models;

import java.time.LocalDate;
import java.util.List;

public class MealPlan {
   
    private int id;
    private String ownerId;
    private String name;
    private String startDate;
    private String endDate;
    private List<MealPlanEntry> entries;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getStartDate() {
        return startDate;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public List<MealPlanEntry> getEntries() {
        return entries;
    }
    public void setEntries(List<MealPlanEntry> entries) {
        this.entries = entries;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void addMealPlanEntry(MealPlanEntry m) {
        entries.add(m);
    }
    

}
