package com.mealplan;
import com.mealplan.controllers.ImportFoodHandler;
import com.mealplan.controllers.MealPlanEntryHandler;
import com.mealplan.controllers.MealPlanHandler;
import com.mealplan.controllers.RecipeHandler;
import com.mealplan.services.FoodService;
import com.mealplan.services.MealPlanEntryService;
import com.mealplan.services.MealPlanService;
import com.mealplan.services.RecipeService;
import com.mealplan.controllers.FoodHandler;
import com.mealplan.auth.User;
import com.mealplan.dao.FoodDao;
import com.mealplan.dao.JdbcFoodDao;
import com.mealplan.dao.MealPlanDao;
import com.mealplan.dao.MealPlanEntryDao;
import com.mealplan.dao.RecipeDao;
import com.mealplan.models.MealPlanEntry;
import com.mealplan.auth.LoginHandler;
import com.mealplan.auth.SessionStore;
import com.mealplan.auth.CallBackHandler;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import com.sun.net.httpserver.Headers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.postgresql.ds.PGSimpleDataSource;


/**
 * This is the entry point for the MealPlan application. This class
 * starts the HTTP server, registers handlers, defines routing,
 * central entry point for backend
*/
public class Main {
    /*********************************
     * Database URL/User/Password
     *********************************/
    /** JDBC Connection URL */
    private static final String URL = System.getenv("DB_URL");
    /** Database Username */
    private static final String USER = System.getenv("DB_USER");
    /** Database Password */
    private static final String PASS = System.getenv("DB_PASSWORD");


    /**
     * Main class for the MealPlan application
     * @param args cmd line arguments.
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        // DataSource
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        // Set the URL, User, and password for the database.
        dataSource.setURL(URL);
        dataSource.setUser(USER);
        dataSource.setPassword(PASS);

        // Initialize the dao (communicates with database), handlers (food, meal plan, and recipe handlers),
        // and services (handles http communcation for food, meal plans, and recipes)
        System.out.println("Initializing Database, Services, and Handlers");
        FoodDao foodDao = new JdbcFoodDao(dataSource);
        FoodService foodService = new FoodService(foodDao);
        FoodHandler foodHandler = new FoodHandler(foodService);

        MealPlanDao mealPlanDao = new MealPlanDao(dataSource);
        MealPlanEntryDao mealPlanEntryDao = new MealPlanEntryDao(dataSource);
        MealPlanService mealPlanService = new MealPlanService(mealPlanDao, mealPlanEntryDao);
        MealPlanHandler mealPlanHandler = new MealPlanHandler(mealPlanService);

        RecipeDao recipeDao = new RecipeDao(dataSource);
        RecipeService recipeService = new RecipeService(recipeDao);
        RecipeHandler recipeHandler = new RecipeHandler(recipeService);

        
        MealPlanEntryService mealPlanEntryService = new MealPlanEntryService(mealPlanEntryDao, mealPlanDao, recipeDao);
        MealPlanEntryHandler mealPlanEntryHandler = new MealPlanEntryHandler(mealPlanEntryService);



        // Testing only: clears the db table
        if ("true".equals(System.getenv("TEST_MODE"))) {
            System.out.println("TEST_MODE active — clearing all tables in PostgreSQL ...");
            foodDao.clearFoodsTable();
            // mealPlanEntryDao.clearMealPlanEntryTable();
            mealPlanDao.clearMealPlanTable();
            recipeDao.clearRecipeTable();
        }

        // Initialize Firebase for Google OAuth tokens
        FileInputStream serviceAccount = new FileInputStream("serviceAccountKey.json");
        FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
        FirebaseApp.initializeApp(options);

        // Server setup - Starts the server on port 8080. server will be: http://localhost:8080
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080), 0);

        //Define endpoint. When someone visits /health, they will recieve "Status: OK"
        server.createContext("/health", exchange -> {
            String json = "{\"status\":\"ok\"}";
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        
        // login and callback sessions
        server.createContext("/auth/login", new LoginHandler());
        server.createContext("/auth/callback", new CallBackHandler());        
        
        // Protected endpoint
        server.createContext("/api/protected", new ProtectedHandler());
        
        // register handler --> API handler
        server.createContext("/api/import", new ImportFoodHandler());
        
        // register handler for food endpoints --> further refined in FoodHandler
        server.createContext("/api/foods", foodHandler);
        server.createContext("/api/foods/", foodHandler);

        // recipe endpoints
        server.createContext("/api/recipes", recipeHandler);
        server.createContext("/api/recipes/", recipeHandler);

        // meal plan  endpoints
        server.createContext("/api/mealPlans", mealPlanHandler);
        server.createContext("/api/mealPlans/",  mealPlanHandler);

          // meal plan entry endpoints
        server.createContext("/api/mealPlanEntry", mealPlanEntryHandler);
        server.createContext("/api/mealPlanEntry/", mealPlanEntryHandler);
       
        server.setExecutor(null);
        server.start();
        System.out.println("Server running on http://localhost:8080");
    }

    /**
     * Authentication middleware
     */
    static class ProtectedHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (method.equalsIgnoreCase("OPTIONS")) {
                Headers h = exchange.getResponseHeaders();
                h.add("Access-Control-Allow-Origin", "http://localhost:5173");
                h.add("Access-Control-Allow-Credentials", "true");
                h.add("Access-Control-Allow-Methods", "GET, OPTIONS");
                h.add("Access-Control-Allow-Headers", "Authorization, Content-Type");
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:5173");
            exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");

            User user = requireAuth(exchange);
            if (user == null) return; // middleware already sent 401

            // Protected response
            String json = "{ \"message\": \"You accessed a protected endpoint!\", \"email\": \"" +
                    (user.getEmail() != null ? user.getEmail() : "") + "\" }";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            sendText(exchange, 200, json);
        }
    }

    /**
     *  Helper method --> send JSON response from handlers. 
     */ 
    public static void sendText(HttpExchange exchange, int status, String body) throws IOException {
        // convert to byte array
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        // sends HTTP status code
        exchange.sendResponseHeaders(status, bytes.length);
        // opens response stream, writes bytes
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /**
     * Parse quert into Map and returns
     * */
    public static Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null || query.isEmpty()) return result;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2) {
                String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * Authentication middleware
     * Reads sessionId cookie from request
     * Look up associated user in session store
     * Return authenticated User object
     * @param exchange HTTP exchange
     * @return authenticated user
     * @throws IOException
     */
    public static User requireAuth(HttpExchange exchange) throws IOException {
        // ERROR CHECKING
        System.out.println("Authenticating...");
        if ("true".equals(System.getenv("TEST_MODE"))) {
            System.out.println("TEST MODE");
            User testUser = new User();
            testUser.setEmail("test@example.com");
            String testUserId = exchange.getRequestHeaders().getFirst("X-Test-User");
            if (testUserId == null) {
                testUserId = "default-test-user";
            }
            testUser.setId(testUserId);
            testUser.setEmail(testUserId + "@example.com");
            exchange.setAttribute("user", testUser);
            return testUser;
        }
        // Get Cookie
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");

        // ERROR CHECKING
        // System.out.println(authHeader);
        if (cookieHeader == null) {
            //ERROR CHECKING
            // System.out.println("Null header");
            sendUnauthorized(exchange);
            return null;
        }

        // Extract Session ID Cookie
        String sessionID = null;
        for (String c : cookieHeader.split(";")) {
            c = c.trim();
            if (c.startsWith("sessionId=")) {
                sessionID = c.substring("sessionId=".length());
                break;
            }
        }
        if (sessionID == null) {
            //ERROR CHECKING
            // System.out.println("Null header");
            sendUnauthorized(exchange);
            return null;
        }

        // Look up user in SessionStore
        User user = SessionStore.getSessionsById().get(sessionID);

        if (user == null) {
            // ERROR CHECKING
            // System.out.println("Invalid session");
            sendUnauthorized(exchange);
            return null;
        }

        return user;
    }

    // Helper method to send 401 Unauthorized response
    static void sendUnauthorized(HttpExchange exchange) throws IOException {
        String json = "{ \"error\": \"Unauthorized\" }";
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(401, json.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes());
        }
    }
}




