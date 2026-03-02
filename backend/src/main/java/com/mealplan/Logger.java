package com.mealplan;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * Logs info and error messages
 */
public class Logger {
    private static final String LOG_FILE = "C:\\Users\\madis\\SWENG_Capstone\\logs\\backend.log";


    // Structured log
    public static void info (String message, String requestId) {
        String json = String.format(
            "{ \"timestamp\": \"%s\", \"level\": \"INFO\", \"requestId\": \"%s\", \"message\": \"%s\" }",
            Instant.now().toString(), requestId, message);
        System.out.println(json);
        writeToFile(json);

    }


    public static void error (String message, String requestId, Exception e) {
        String json = String.format(
            "{ \"timestamp\": \"%s\", \"level\": \"ERROR\", \"requestId\": \"%s\", \"message\": \"%s\", \"error\": \"%s\" }",
            Instant.now().toString(), requestId, message, e.getMessage());
        System.out.println(json);
        writeToFile(json);
    }

    // Generate a correlation/request ID for each request
    public static String newRequestId() {
        return UUID.randomUUID().toString();
    }

    private static void writeToFile(String line) {
    try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
        fw.write(line + System.lineSeparator());
    } catch (IOException e) {
        System.err.println("Failed to write log file: " + e.getMessage());
    }
}

}

