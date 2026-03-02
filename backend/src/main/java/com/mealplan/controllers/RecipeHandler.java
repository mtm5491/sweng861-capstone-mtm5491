package com.mealplan.controllers;

import com.mealplan.Main;
import com.mealplan.Logger;
import com.mealplan.auth.User;
import com.mealplan.models.Recipe;
import com.mealplan.services.RecipeService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.json.JSONObject;
import java.util.Optional;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;

/**
 * Handles all CRUD operations for /api/recipe
 */
public class RecipeHandler implements HttpHandler {      
    private String path;
    private final RecipeService service;
    
    public RecipeHandler(RecipeService service) {
        this.service = service;
        System.out.println("Recipe Handler created...");
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // System.out.println("RAW BODY = " + new String(exchange.getRequestBody().readAllBytes()));
        String method = exchange.getRequestMethod();
        path = exchange.getRequestURI().getPath();
        Headers h = exchange.getResponseHeaders();
        /**
         * CORS Preflight handling
         * ***THIS FIXED A LOT OF ISSUES WITH AUTHENTICATION ***
         */
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
            // ERROR CHECKING
            // System.out.println("Null user");
            return;
        }
        String ownerId = user.getId();
        if (ownerId == null) {
            return;
        }
        String[] pathParts = path.split("/");
        Integer id = null;

        // Expecting: /api/foods/{id}
        // System.out.println("Raw path: " + exchange.getRequestURI().getPath());
        String[] parts = exchange.getRequestURI().getPath().split("/");
        for (int i = 0; i < parts.length; i++) {
            System.out.println("parts[" + i + "] = '" + parts[i] + "'");
        }
        if (pathParts.length >= 4 && !pathParts[3].isEmpty()) {
            try {
                id = Integer.parseInt(parts[3].trim());
                System.out.println("Parsed ID: " + id);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                // // Record metrics
                // long latency = System.currentTimeMillis() - start;      
                // Metrics.recordRequest(path, 400);
                // Metrics.recordLatency(path, latency);
                
                Main.sendText(exchange, 400, "{ \"error\": \"Invalid food ID\" }");
                return;
            }
        }

        /** Route to operation by method */
        switch (method) {
            case "GET":
                if (id == null) {
                    handleGetAll(exchange, ownerId);
                } else {
                    handleGetById(exchange, id, ownerId);
                }
                break;
            case "POST":
                handleCreate(exchange, ownerId);
                break;
            case "PUT":
                if (id == null) {
                    Main.sendText(exchange, 400, "{ \"error\": \"Missing food ID for update\" }");
                } else {

                    handleUpdate(exchange, id, ownerId);
                }
                break;
            case "DELETE":
                if (id == null) {
                    Main.sendText(exchange, 400, "{ \"error\": \"Missing food ID for delete\" }");
                } else {
                    handleDelete(exchange, id, ownerId);
                }
                break;
            default:
                Main.sendText(exchange, 405, "{ \"error\": \"Method Not Allowed\" }");
        }
    }

    /**
     * GET Operation
     * routes to /api/foods and lists all food associated with owner
     * @param exchange
     * @param owner_id
     * @throws IOException
     */
    private void handleGetAll(HttpExchange exchange, String owner_id) throws IOException {
        // Generate an Id
        String requestId = Logger.newRequestId();
        // Log request
        Logger.info(String.format(
            "Incoming request %s %s", exchange.getRequestMethod(), exchange.getRequestURI()),
            requestId);
        
        // Get and send recipes
        List <Recipe> recipes = service.getAllRecipes(owner_id);
        sendJson(exchange, 200, recipes);
    }

    /**
     * GET Operation for single item
     * routes to /api/foods/{id} and lists single food associated with owner
     * @param exchange
     * @param id
     * @param owner_id
     * @throws IOException
     */
    private void handleGetById(HttpExchange exchange, int id, String owner_id) throws IOException {
        Optional<Recipe> recipe = service.getRecipeById(owner_id, id);
        String requestId = Logger.newRequestId();
        // Log request
        Logger.info(String.format(
        "Incoming request %s %s", exchange.getRequestMethod(), exchange.getRequestURI()),
        requestId);
        if (recipe.isPresent()) {

            Recipe r = recipe.get();
            JSONObject obj = new JSONObject();
            obj.put("id", r.getId());
            obj.put("name", r.getName());
            obj.put("description", r.getDescription());
            obj.put("instructions", r.getInstructions());
            obj.put("ingredients", r.getIngredients());
            
            Logger.info(String.format("Responded with status %d", 200),
            requestId);
            // Record metrics
            // long latency = System.currentTimeMillis() - start;
            // Metrics.recordRequest(path, 200);
            // Metrics.recordLatency(path, latency);
            sendJson(exchange, 200, obj);
        } else {
            Logger.info(String.format("Responded with status %d", 404),
            requestId);
            // Record metrics
            // long latency = System.currentTimeMillis() - start;
            // Metrics.recordRequest(path, 404);
            // Metrics.recordLatency(path, latency);
            Main.sendText(exchange, 404, "{ \"error\": \"Food not found\" }");
            return;
        }

    }

    /**
     * POST Operation
     * routes to /api/foods to create a new food
     * @param exchange
     * @param owner_id
     * @throws IOException
     */
    private void handleCreate(HttpExchange exchange, String owner_id) throws IOException {
        // ERROR CHECKING
        // System.out.println(">>> handleCreate reached with user ID:" + owner_id);
        String requestId = Logger.newRequestId();
        // Log request
        Logger.info(String.format(
        "Incoming request %s %s", exchange.getRequestMethod(), exchange.getRequestURI()),
        requestId);
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        // ERROR CHECKING
        // System.out.println(">>> Raw body: " + body);
        
        Gson gson = new Gson();
        Recipe recipe = gson.fromJson(body, Recipe.class);
        System.out.println("Parsed description = " + recipe.getDescription());
        recipe.setOwnerId(owner_id);
        // ERROR CHECKING
        // System.out.println(">>> Parsed food: " + food);

        try {
            Recipe created = service.createRecipe(recipe);
            JSONObject obj = new JSONObject(created);
            
                // Record metrics
            // long latency = System.currentTimeMillis() - start;
            // Metrics.recordRequest(path, 201);
            // Metrics.recordLatency(path, latency);
            sendJson(exchange, 201, obj.toString());
            Logger.info(String.format("Responded with status %d", 201),
            requestId);
            
         } catch (IllegalArgumentException | IllegalStateException e) {
            // Record metrics
            // long latency = System.currentTimeMillis() - start;
            // Metrics.recordRequest(path, 400);
            // Metrics.recordLatency(path, latency);
            Logger.info(String.format("Responded with status %d", 400),
            requestId);
            Main.sendText(exchange, 400, "{ \"error\": \"Invalid food data\" }");
         }     
    }

    /**
     * PUT Operation
     * routes to /api/food/{id} to update an existing food
     * @param exchange
     * @param id
     * @param owner_id
     * @throws IOException
     */
    private void handleUpdate(HttpExchange exchange, Integer id, String owner_id) throws IOException {
        // Food existing = service.getFoodById(id);
        // String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        // JSONObject obj = new JSONObject(body);
        Recipe recipe = new Recipe();
        recipe.setId(id);
        recipe.setOwnerId(owner_id);
        try {
            boolean updated = service.updateRecipe(id, recipe, owner_id);
            if (updated) {
                // Record metrics
                // long latency = System.currentTimeMillis() - start;
                // Metrics.recordRequest(path, 404);
                // Metrics.recordLatency(path, latency);
                Main.sendText(exchange, 404, "{ \"error\": \"Food not found\" }");
            } else {
                // Record metrics
                // long latency = System.currentTimeMillis() - start;
                // Metrics.recordRequest(path, 404);
                // Metrics.recordLatency(path, latency);
                Main.sendText(exchange, 404, "{ \"error\": \"Food not found\" }");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            // // Record metrics
            // long latency = System.currentTimeMillis() - start;
            // Metrics.recordRequest(path, 400);
            // Metrics.recordLatency(path, latency);
            Main.sendText(exchange, 400, "{ \"error\": \"" + e.getMessage() + "\" }");
        }
    }

    /**
     * DELETE Operation
     * routes to /api/foods/{id} to delete food item
     * @param exchange
     * @param id
     * @param owner_id
     * @throws IOException
     */
    private void handleDelete(HttpExchange exchange, int id, String owner_id) throws IOException {
        boolean deleted = service.deleteFood(id, owner_id);
        if (deleted) {
            // Record metrics
            // long latency = System.currentTimeMillis() - start;
            // Metrics.recordRequest(path, 204);
            // Metrics.recordLatency(path, latency);
            Main.sendText(exchange, 204, "");
        } else {
            // Record metrics
            // long latency = System.currentTimeMillis() - start;
            // Metrics.recordRequest(path, 404);
            // Metrics.recordLatency(path, latency);
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

        // String json = data.toString();
        // exchange.getResponseHeaders().add("Content-Type", "application/json");
        // exchange.sendResponseHeaders(statusCode, json.getBytes().length);
        // OutputStream os = exchange.getResponseBody();
        // os.write(json.getBytes());
        // os.close();

        Gson gson = new Gson();
        String json = gson.toJson(data);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, json.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes());
        }

    }
}

