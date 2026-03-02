package com.mealplan.auth;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all active user sessions
 * In memory session store for mapping sessionID to user and looking up
 * logged in user based on session cookie
 * 
 * resets when server starts --> needs to be replaced with database
 */
public class SessionStore {
    // session ID --> User
    static Map<String, User> sessionsById = new HashMap<>();

    /**
     * Retrieve session map
     * @return session map
     */
    public static Map<String, User> getSessionsById() {
        return sessionsById;
    }

    /**
     * Add or update a session
     * @param sessionsById
     */
    public static void setSessionsById(Map<String, User> sessionsById) {
        SessionStore.sessionsById = sessionsById;
    }

    public static void addSession(String sessionId, User user) {
        sessionsById.put(sessionId, user);
    }

    public static User getUser(String sessionId) {
        return sessionsById.get(sessionId);
    }

    public static void removeSession(String sessionId) {
        sessionsById.remove(sessionId);
    }


}
