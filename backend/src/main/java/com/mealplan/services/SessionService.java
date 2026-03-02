package com.mealplan.services;

import com.sun.net.httpserver.HttpExchange;
import com.mealplan.auth.User;
import com.mealplan.auth.SessionStore;

/**
 * Providers helpers for reading logged-in user from session cookie
 * extracts sessionID cookie from request, looks up assocated user in session store, 
 * and returns local ID for user
 */
public class SessionService {

    /**
     * retrieves user ID from session
     * @param exchange HTTP request
     * @return user ID
     */
    public static String getUserIdFromSession(HttpExchange exchange) {
        String sessionId = getSessionIdFromCookies(exchange);
        if (sessionId == null) return null;
        User user = SessionStore.getSessionsById().get(sessionId);
        if (user == null) return null;
        return user.getId(); 
    }

    /**
     * retrieves session ID from cookie
     * @param exchange HTTP request
     * @return session ID
     */
    private static String getSessionIdFromCookies(HttpExchange exchange) {
        var headers = exchange.getRequestHeaders();
        if (!headers.containsKey("Cookie")) return null;
        for (String cookie : headers.get("Cookie")) {
            for (String part : cookie.split(";")) {
                part = part.trim();
                if (part.startsWith("sessionId=")) {
                    return part.substring("sessionId=".length());
                }
            }
        }
        return null;
    }
}

