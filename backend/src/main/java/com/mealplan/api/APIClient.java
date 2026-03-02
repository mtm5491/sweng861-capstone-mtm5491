package com.mealplan.api;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
/**
 * Builds Request to API, calls API, and returns raw JSON
 */

    public class APIClient {    

    // public static final String API_URL = "https://api.nal.usda.gov/fdc/v1/foods/search?query=apple&api_key";
    public static final String API_KEY = "draRUJ0KBDWrQGyw5YfFg0clMI2blBy6K3mj4HsS";
    public static final String API_SEARCH_URL = "https://api.nal.usda.gov/fdc/v1/foods/search?query=";
    public static final String API_DETAILS_URL = "https://api.nal.usda.gov/fdc/v1/food/";
    // System Env. Variable Option
    //public static final String API_KEY = System.getenv(USDA_API_KEY));

    // Search endpoint for fdcID
    public static String searchFood(String query) throws IOException {
        // System.out.println("API CLIENT: Seaching food for query: " + query);
        String urlStr = API_SEARCH_URL + query + "&api_key=" + API_KEY;
        // System.out.println("API CLIENT: Search URL: " + urlStr);
        return fetch(urlStr);
    }

    // Food details endpoint with fdcID
    public static String foodDetails(int fdcID) throws IOException {
        String urlStr = API_DETAILS_URL + fdcID + "?api_key=" + API_KEY;
        return fetch(urlStr);
    }

    // Get data from the API
    public static String fetch(String urlStr) throws IOException {
        // create URL
        URL url = new URL(urlStr);
        // open connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // set request
        conn.setRequestMethod("GET");
        
        try (InputStream is = conn.getInputStream()) {
            return new String (is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
