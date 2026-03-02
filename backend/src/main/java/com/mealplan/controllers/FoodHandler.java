package com.mealplan.controllers;

import com.mealplan.Logger;
import com.mealplan.Main;
import com.mealplan.auth.User;
import com.mealplan.services.SessionService;
import com.mealplan.models.Food;
import com.mealplan.services.FoodService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Optional;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;

/**
 * Handles all CRUD operations for /api/foods endpoint
 * 
 * Supports:
 *   GET    /api/foods          -> list all foods for logged-in user
 *   GET    /api/foods/{id}     -> get single food 
 *   POST   /api/foods          -> create food
 *   PUT    /api/foods/{id}     -> update food
 *   DELETE /api/foods/{id}     -> delete food
 */
public class FoodHandler implements HttpHandler {  
    private String path;
    private final FoodService service;

    public FoodHandler(FoodService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        
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

        // ERROR CHECKING
        // System.out.println(">>> Entered FoodHandler");
        // System.out.println("Method: " + exchange.getRequestMethod());
        // System.out.println("Path: " + exchange.getRequestURI().getPath());
        // System.out.println("Headers: " + exchange.getRequestHeaders());

        /**
         * AUTHENTICATION
         */
        User user = Main.requireAuth(exchange);
        if (user == null) {
            return;
        }
        String ownerId = user.getId();
        if (ownerId == null) {
            Main.sendText(exchange, 401, "{ \"error\": \"Unauthorized\" }");
            return;
        }
        
        // /**
        //  * PARSE ID FROM PATH
        //  */
        // String path = exchange.getRequestURI().getPath();
        // /api/food or /api/food/id
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
                    // ERROR CHECKING   
                    //System.out.println(">>> Doing GET ALL");
                    handleGetAll(exchange, ownerId);
                } else {
                    // ERROR CHECKING
                    // System.out.println(">>> Doing GET BY ID");
                    handleGetById(exchange, id, ownerId);
                }
                break;
            case "POST":
                // ERROR CHECKING
                // System.out.println(">>> Doing POST");
                handleCreate(exchange, ownerId);
                break;
            case "PUT":
                if (id == null) {
                    // Record metrics
                    // long latency = System.currentTimeMillis() - start;
                    // Metrics.recordRequest(path, 400);
                    // Metrics.recordLatency(path, latency);
                    Main.sendText(exchange, 400, "{ \"error\": \"Missing food ID for update\" }");
                } else {
                    // ERROR CHECKING
                    // System.out.println(">>> Doing PUT");
                    handleUpdate(exchange, id, ownerId);
                }
                break;
            case "DELETE":
                if (id == null) {
                    // long latency = System.currentTimeMillis() - start;
                    // // Record metrics
                    // Metrics.recordRequest(path, 400);
                    // Metrics.recordLatency(path, latency);
                    Main.sendText(exchange, 400, "{ \"error\": \"Missing food ID for delete\" }");
                } else {
                    // ERROR CHECKING
                    // System.out.println(">>> Doing DELETE");
                    handleDelete(exchange, id, ownerId);
                }
                break;
            default:
                // Record metrics
                // long latency = System.currentTimeMillis() - start;
                // Metrics.recordRequest(path, 405);
                // Metrics.recordLatency(path, latency);
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
        // ERROR CHECKING
        // System.out.println("Owner ID used for GET: " + owner_id);
        List <Food> foods = service.getAllFoods(owner_id);
        // JSONArray arr = new JSONArray(foods);
        // Record metrics
        // long latency = System.currentTimeMillis() - start;
        // Metrics.recordRequest(path, 200);
        // Metrics.recordLatency(path, latency);
        sendJson(exchange, 200, foods);
    }

    /**
     * GET Operation for single item
     * routes to /api/foods/{id} and lists single food associated with owner
     * @param exchange
     * @param id
     * @param owner_id
     * @throws IOException
     */
    private void handleGetById(HttpExchange exchange, Integer id, String owner_id) throws IOException {
        System.out.println("Handle get by ID" + id + "; owner:" + owner_id);
        // Optional --> Might return or not
        Optional<Food> food = service.getFoodById(owner_id, id);
        String requestId = Logger.newRequestId();
        // Log request
        Logger.info(String.format(
        "Incoming request %s %s", exchange.getRequestMethod(), exchange.getRequestURI()),
        requestId);
        if (food.isPresent()) {
            
            // System.out.println("FOOD OBJECT = " + food.get());
            // JSONObject obj = new JSONObject(food.get());
            // sendJson(exchange, 200, obj.toString());
            Food f = food.get();
            JSONObject obj = new JSONObject();
            obj.put("id", f.getId());
            obj.put("fdcId", f.getFdcId());
            obj.put("description", f.getDescription());
            System.out.println("Getting by ID, Description:" + f.getDescription());
            obj.put("foodNutrients", f.getFoodNutrients());
            obj.put("ownerID", f.getOwnerID());
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
        Food food = gson.fromJson(body, Food.class);
        System.out.println("Parsed description = " + food.getDescription());
        food.setOwnerID(owner_id);
        // ERROR CHECKING
        // System.out.println(">>> Parsed food: " + food);

        try {
            Food created = service.createFood(food);
            JSONObject obj = new JSONObject(created);
            
            //     // Record metrics
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
        Food food = new Food();
        food.setId(id);
        food.setOwnerID(owner_id);
        try {
            boolean updated = service.updateFood(id, food, owner_id);
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
    private void handleDelete(HttpExchange exchange, Integer id, String owner_id) throws IOException {
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

