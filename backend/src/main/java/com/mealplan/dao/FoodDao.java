package com.mealplan.dao;

import com.mealplan.models.Food;
import java.util.List;
import java.util.Optional;
/**
 * Data access object interface
 * Simulates a database for unit tests to ensure food items are updated, created, etc. properly.
 */
public interface FoodDao {
    
    boolean existsByDescription(String ownerId, String description);
    Food insert(Food food);
    List<Food> findByOwner(String ownerId);
    Optional<Food> findById(int id, String owner_id);
    boolean update(Food food);
    boolean delete(int id, String ownerId);
    public void clearFoodsTable();

}


