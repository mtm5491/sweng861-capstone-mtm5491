package com.mealplan.auth;

import com.sun.net.httpserver.HttpExchange;
import java.util.UUID;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import com.sun.net.httpserver.HttpHandler;

/**
 * /auth/login handlers: exchange code for tokens, validate, create local session.
 * Start login: redirect to google.
 */
public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try { 
            String clientId = System.getenv("GOOGLE_CLIENT_ID");

            if (clientId == null || clientId.isBlank()) {
                System.err.println("ERROR: GOOGLE_CLIENT_ID is not set.");
                redirect(exchange, "http://localhost:5173/login?error=failed");
                return;
            }

            // ERROR CHECKING
            // System.out.println("CLIENT ID BEING USED: " + clientId);

            // Build Google OAuth URL
            String redirectUri = URLEncoder.encode("http://localhost:8080/auth/callback", StandardCharsets.UTF_8);
            String scope = URLEncoder.encode("openid email profile", StandardCharsets.UTF_8);
            // Generate random state value
            String state = UUID.randomUUID().toString(); 

            // Build google authorization URL
            String url = "https://accounts.google.com/o/oauth2/v2/auth"
                    + "?client_id=" + clientId
                    + "&redirect_uri=" + redirectUri
                    + "&response_type=code"
                    + "&scope=" + scope
                    + "&state=" + state;

            // Redirect user to Google login
            redirect(exchange, url);
            // exchange.getResponseHeaders().add("Location", url);
            // exchange.sendResponseHeaders(302, -1);
            // exchange.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            redirect (exchange, "http://localhost:5173/login?error=failed");
        }
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
}
