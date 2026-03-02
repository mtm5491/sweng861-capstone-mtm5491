package com.mealplan.auth;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all known users to app; in memory only, will need to be updated to database
 */
public class UserStore {
        static Map<String, User> usersByProviderId = new HashMap<>();
}
