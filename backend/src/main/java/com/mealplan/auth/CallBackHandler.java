package com.mealplan.auth;

import com.mealplan.Main;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.UUID;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Base64;
import java.util.Map;
// HTTP server tool
import java.time.Instant;

/**
 * /auth/callback handler: exchange code for tokens, validate, create local session.
 * Handle callback from google login.
 */
public class CallBackHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {    
        /* 1. PARSE CODE */
            
            //  query string from callback URL  
            //  URL will look something like auth/callback?code=4/0AY0e-g7...&state=xyz and getQuery() 
            //  will return the part after the ? 
            String query = exchange.getRequestURI().getQuery();
            
            // parse params to extract code
            Map<String, String> params = Main.parseQuery(query);
            String code = params.get("code");
            
            // ERROR CHECKING
            // System.out.println("Callback received: code=" + code);

            // Error message for null code
            if (code == null) {
                Main.sendText(exchange, 400, "Missing code");
                return;
            }
            /** 2. EXCHANGE CODE FOR TOKENS */

            // Build POST request to Google's token endpoint --> Must match what was used in auth/login
            String tokenEndpoint = "https://oauth2.googleapis.com/token";
            String clientId = System.getenv("GOOGLE_CLIENT_ID");
            String clientSecret = System.getenv("GOOGLE_CLIENT_SECRET");
            String redirectUri = "http://localhost:8080/auth/callback";
            
            // construct POST body
            String body = "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                    + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                    + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
                    + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                    + "&grant_type=authorization_code";

            // Send POST request to google
            // Open connection, set it to POST, send encoded body
            URL url = new URL(tokenEndpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }

            // read google's response token
            String responseJson;
            try (InputStream is = conn.getInputStream()) {
                responseJson = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
            // ERROR HANDLING
            // System.out.println("Token response: " + responseJson);

            /** 3. EXTRACT ID_TOKEN */
            // JSON parse for id_token
            String idToken = extractJsonField(responseJson, "id_token");
            if (idToken == null) {
                Main.sendText(exchange, 500, "No id_token in response");
                return;
            }

            /** 4. DECODE ID TOKEN PAYLOAD */
            // Decode ID Token
            String[] parts = idToken.split("\\.");
            if (parts.length < 2) {
                Main.sendText(exchange, 500, "Invalid id_token format");
                return;
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            System.out.println("ID Token payload: " + payloadJson);

            // Extract user identity fields 
            String sub = extractJsonField(payloadJson, "sub");  // User ID
            String email = extractJsonField(payloadJson, "email");  // User email

            if (sub == null || email == null) {
                redirect(exchange, "http://localhost:5173/login?error=failed");
                return;
            }

            // ERROR CHECKING
            // System.out.println("Google ID token validated via iss/aud checks.");

            /** 5. CREATE/UPDATE LOCAL USER */
            // if the user doesn't exist, create a new one; otherwise, update the existing user
            User user = UserStore.usersByProviderId.get(sub);
            if (user == null) {
                user = new User();
                user.id = UUID.randomUUID().toString();
                user.providerId = sub;
                user.email = email;
                user.createdAt = Instant.now();
                user.updatedAt = Instant.now();
                UserStore.usersByProviderId.put(sub, user);
                System.out.println("Created new user: " + user.id + " (" + email + ")");
            } else {
                user.updatedAt = Instant.now();
                System.out.println("Updated existing user: " + user.id + " (" + email + ")");
            }

            /** 6. CREATE SESSION COOKIE */


            String sessionId = UUID.randomUUID().toString();
            SessionStore.sessionsById.put(sessionId, user);

            // Set the cookie
            exchange.getResponseHeaders().add("Set-Cookie", "sessionId=" + sessionId + "; HttpOnly; Path=/; SameSite=Lax");
            // exchange.getResponseHeaders().add("Location", "http://localhost:5173/foods");
            // exchange.sendResponseHeaders(302, -1);

            /** 7. REDIRECT TO FRONTEND */
            redirect(exchange, "http://localhost:5173/food");

        } catch (Exception e) {
            redirect(exchange, "http://localhost:5173/login?error=failed");
        }    
        // Shows successful login message
        // String response = "Login successful. You can close this window.";
        // Main.sendText(exchange, 200, response);
    }

    /**
     * Redirect Helper
     * @param exchange
     * @param url
     * @throws IOException
     */
    private void redirect(HttpExchange exchange, String url) throws IOException {
        exchange.getResponseHeaders().set("Location", url);
        exchange.sendResponseHeaders(302, -1);
    }

    
    /**
     * extract JSON Helper
     * @param json
     * @param field
     * @return
     */
    private static String extractJsonField(String json, String field) {
        String search = "\"" + field + "\":";
        int idx = json.indexOf(search);
        if (idx == -1) return null;

        int start = json.indexOf("\"", idx + search.length()) + 1;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

}

