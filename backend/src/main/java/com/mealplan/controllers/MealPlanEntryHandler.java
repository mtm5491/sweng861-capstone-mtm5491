package com.mealplan.controllers;

import com.mealplan.Main;
import com.mealplan.auth.User;
import com.mealplan.Logger;
import com.mealplan.models.MealPlan;
import com.mealplan.models.MealPlanEntry;
import com.mealplan.services.MealPlanEntryService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import java.util.Optional;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;

/**
 * Handles all CRUD operations for /api/mealPlanEntry
 */
public class MealPlanEntryHandler implements HttpHandler {      

    private final MealPlanEntryService service;
    
    public MealPlanEntryHandler(MealPlanEntryService service) {
        this.service = service;
        System.out.println("Meal Plan Handler created...");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        Headers h = exchange.getResponseHeaders();

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            h.add("Access-Control-Allow-Origin", "http://localhost:5173");
            h.add("Access-Control-Allow-Credentials", "true");
            h.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            h.add("Access-Control-Allow-Headers", "Authorization, Content-Type");
            
            exchange.sendResponseHeaders(204, -1);
            return;
        }
        // Standard CORS headers for all other requests
        h.add("Access-Control-Allow-Origin", "http://localhost:5173");
        h.add("Access-Control-Allow-Credentials", "true");

        /**
         * AUTHENTICATION
         */
        User user = Main.requireAuth(exchange);
        if (user == null) {
            return;
        }
        String ownerId = user.getId();
        if (ownerId == null) {
            return;
        }
        
        // /**
        //  * PARSE ID FROM PATH
        //  */
        // String path = exchange.getRequestURI().getPath();
        // /api/mealPlanEntry/{id}
       
        Integer id = null;

        String[] parts = exchange.getRequestURI().getPath().split("/");
        for (int i = 0; i < parts.length; i++) {
            System.out.println("parts[" + i + "] = '" + parts[i] + "'");
        }
        if (parts.length >= 4 && !parts[3].isEmpty()) {
            try {
                id = Integer.parseInt(parts[3].trim());
            } catch (NumberFormatException e) {
                Main.sendText(exchange, 400, "{ \"error\": \"Invalid meal plan entry ID\" }");
                return;
            }
        }


        /** Route to operation by method */
        switch (method) {
            case "GET":
                    handleGetById(exchange, id, ownerId);
                break;
            case "POST":

                handleCreate(exchange, ownerId);
                break;
            case "PUT":
                if (id == null) {
  
                    Main.sendText(exchange, 400, "{ \"error\": \"Missing mealPlanEntry ID for update\" }");
                } else {

                    handleUpdate(exchange, id, ownerId);
                }
                break;
            case "DELETE":
                if (id == null) {

                    Main.sendText(exchange, 400, "{ \"error\": \"Missing mealPlanEntry ID for delete\" }");
                } else {
                    handleDelete(exchange, id, ownerId);
                }
                break;
            default:

                Main.sendText(exchange, 405, "{ \"error\": \"Method Not Allowed\" }");
        }
    }

    /**
     * GET Operation for single item
     * routes to /api/mealPlanEntry/{id} and lists single mealPlanEntry associated with owner
     * @param exchange
     * @param id
     * @param owner_id
     * @throws IOException
     */
    private void handleGetById(HttpExchange exchange, int id, String ownerId) throws IOException {
        // Optional --> Might return or not
        Optional<MealPlanEntry> mealPlanEntry = service.getMealPlanEntryById(id, ownerId);
        String requestId = Logger.newRequestId();
        // Log request
        Logger.info(String.format(
        "Incoming request %s %s", exchange.getRequestMethod(), exchange.getRequestURI()), requestId);
        if (mealPlanEntry.isPresent()) {
            MealPlanEntry m = mealPlanEntry.get();
            JSONObject obj = new JSONObject();
            obj.put("id", m.getId());
            obj.put("mealPlanId", m.getMealPlanId());
            obj.put("recipeId", m.getRecipeId());
            obj.put("date",m.getDate());
            obj.put("mealType",m.getMealType());
            
            Logger.info(String.format("Responded with status %d", 200),
            requestId);

            sendJson(exchange, 200, obj);
        } else {
            Logger.info(String.format("Responded with status %d", 404),
            requestId);

            Main.sendText(exchange, 404, "{ \"error\": \"Food not found\" }");
            return;
        }

    }

    /**
     * POST Operation
     * routes to /api/mealPlanEntry to create a new mealPlanEntry
     * @param exchange
     * @param owner_id
     * @throws IOException
     */
    private void handleCreate(HttpExchange exchange, String ownerId) throws IOException {
        String requestId = Logger.newRequestId();
        // Log request
        Logger.info(String.format(
        "Incoming request %s %s", exchange.getRequestMethod(), exchange.getRequestURI()),
        requestId);
        
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Gson gson = new Gson();
        MealPlanEntry mealPlanEntry;
        try {
            mealPlanEntry = gson.fromJson(body, MealPlanEntry.class);
            System.out.println("Parsed id = " + mealPlanEntry.getId());
        } catch (Exception e) {
            e.printStackTrace();
            Main.sendText(exchange, 400, "{ \"error\": \"JSON parse failed: " + e.getMessage() + "\" }");
            return;
        }
        try {
            MealPlanEntry created = service.createMealPlanEntry(mealPlanEntry, ownerId);
            JSONObject obj = new JSONObject(created);
            sendJson(exchange, 201, obj.toString());
            Logger.info(String.format("Responded with status %d", 201),
            requestId);
            
         } catch (Exception e) {
            Logger.info(String.format("Responded with status %d", 400),
            requestId);
            Main.sendText(exchange, 400, "{ \"error\": \"Invalid mealPlanEntry data\" }");
         }


    }

    /**
     * PUT Operation
     * routes to /api/mealPlanEntry/{id} to update an existing mealPlanEntry
     * @param exchange
     * @param id
     * @param owner_id
     * @throws IOException
     */
    private void handleUpdate(HttpExchange exchange, int id, String ownerId) throws IOException {

        MealPlanEntry mealPlanEntry = new MealPlanEntry();
        mealPlanEntry.setId(id);
        try {
            boolean updated = service.updateMealPlanEntry(id, mealPlanEntry, ownerId);
            if (updated) {
   
                Main.sendText(exchange, 204, "");
            } else {

                Main.sendText(exchange, 404, "{ \"error\": \"Food not found\" }");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {

            Main.sendText(exchange, 400, "{ \"error\": \"" + e.getMessage() + "\" }");
        }
    }

    /**
     * DELETE Operation
     * routes to /api/mealPlanEntry/{id} to delete mealPlanEntry item
     * @param exchange
     * @param id
     * @param owner_id
     * @throws IOException
     */
    private void handleDelete(HttpExchange exchange, int id, String ownerId) throws IOException {
        boolean deleted = service.deleteMealPlanEntry(id, ownerId);
        if (deleted) {

            Main.sendText(exchange, 204, "");
        } else {

            Main.sendText(exchange, 404, "{ \"error\": \"Food not found\" }");
        }
    }

    /**
     * Helper to send JSON response
     * @param exchange
     * @param statusCode
     * @param object to be converted
     * @throws IOException
     */
    private void sendJson(HttpExchange exchange, int statusCode, Object data) throws IOException {

        Gson gson = new Gson();
        String json = gson.toJson(data);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, json.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes());
        }

    }
}

