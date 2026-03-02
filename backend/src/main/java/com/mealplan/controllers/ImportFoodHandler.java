package com.mealplan.controllers;

import com.mealplan.Main;
import com.mealplan.services.ThirdPartyServices;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Reads query parameters from URL to call USDA Food Data, validate/process JSON,
 * and return raw JSON.
 */
public class ImportFoodHandler implements HttpHandler {
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {
            // Exrtact "query" parameter (food to search)
            String query = getQueryParam(exchange, "query");
            if (query == null || query.isEmpty()) {
                Main.sendText(exchange, 400, "{ \"error\": \"Missing query parameter\" }");
                return;
            }
            // Use ThirdPartyServices to import food data
            // search by keyword and retrieve food JSON
            String result = ThirdPartyServices.importFood(query);

            // validate and save to database
            ThirdPartyServices.processFoodJson(result);


            // return raw JSON to client
            Main.sendText(exchange, 200, result);
        } catch (Exception e) {
            Main.sendText(exchange, 500, "{ \"error\": \"Failed to import food data\" }"); 
        }
    }

    /**
     * Extracts single query parameter from URL
     * @param exchange HTTP Request
     * @param param paremtter name to extract
     * @return decoded parameter value, null if missing
     */
    private String getQueryParam(HttpExchange exchange, String param) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return null;
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=");
            if (parts.length == 2 && parts[0].equals(param)) {
                return URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
            }
        }
        return null;
    }
}
