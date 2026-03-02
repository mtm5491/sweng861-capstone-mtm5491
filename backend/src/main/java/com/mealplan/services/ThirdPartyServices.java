package com.mealplan.services;
import com.mealplan.api.APIClient;
import org.json.JSONObject;

/**
 * Handles interactons with USDA API
 * searches for food by keyword, extract fdcId, fetch detauls, validate JSON, save food
 * to database
 */
public class ThirdPartyServices {

    /**
     * Import food from USDA API
     * @param query keyword to search
     * @return JSON String of details
     * @throws Exception
     */
    public static String importFood(String query) throws Exception {
        // ERROR CHECKING
        // System.out.println("STEP 1: Calling search endpoint...");
        // System.out.println("Query: " + query);

        // 1. SEARCH FOR FOOD
        String searchJSON = APIClient.searchFood(query);

        // ERROR CHECKING  
        // System.out.println("Search JSON: " + searchJSON);
        // System.out.println("STEP 2: Extracting fdcID from search results...");
        
        // 2. EXTRACT FCDID FROM SEARCH RESULTS
        int fdcID = extractFdcID(searchJSON);
        
        // ERROR CHECKING
        // System.out.println("STEP 3: Calling details endpoint...");
        
        // 3. GET DETAILS USING FCDID
        String detailsJSON = APIClient.foodDetails(fdcID);
        
        // ERROR CHECKING
        // System.out.println("Details JSON: " + detailsJSON);

        // 4. RETURN DETAILS
        return detailsJSON;
    }

    /**
     * Validates JSON String
     * @param json
     * @return true if validated, false otherwise
     */
    public static boolean validateFoodJson(String json) {
        // convert to lowercase
        String lower = json.toLowerCase();

        // check that it contains ID, Description, Nutrients
        return lower.contains("\"fdcid\"")
        && lower.contains("\"description\"")
        && lower.contains("\"foodnutrients\"");
    }

    /**
     * Extract FCD ID from JSON String
     * @param JSON response from API
     * @return FCD ID integer
     */
    private static int extractFdcID(String JSON) {
        // look for "fdcID": number
        String [] parts = JSON.split("\"fdcId\":");
        String number = parts[1].split(",")[0].trim();
        return Integer.parseInt(number);
    }

    /**
     * Validates JSON and saves in Database
     * @param json
     */
    public static void processFoodJson(String json) {
        if (validateFoodJson(json)) {
            saveFood(json);
    }
}

    /**
     * Saves Food information into database
     * TODO: Create another table to save USDA data to, that can be accessed by all users
     * @param json
     */
    public static void saveFood(String json) {
        JSONObject obj = new JSONObject(json);
        int fdcID = obj.getInt("fdcId");
        String description = obj.getString("description");
        String nutrientData = obj.getJSONArray("foodNutrients").toString(); 
        DatabaseService.saveFood(fdcID, description, nutrientData);
    }

}
