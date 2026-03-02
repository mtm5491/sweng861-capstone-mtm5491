package com.mealplan.models;

import java.util.Date;
import java.util.List;

public class Day {
	String dayID;
	Date date;
	MealPlanEntry breakfast;
	MealPlanEntry lunch;
	MealPlanEntry dinner;
	List<MealPlanEntry> snacks;
	
	
	// void addEntry(MealPlanEntry entry) {
	// 	String mealType = entry.getMealType();
	// 	switch (mealType) {
	// 		case "breakfast":
	// 			breakfast = entry;
	// 		case "lunch":
	// 			lunch = entry;
	// 		case "dinner":
	// 			dinner = entry;
	// 		case "snack":
	// 			snacks.add(entry);
	// 	}
	// }
}

