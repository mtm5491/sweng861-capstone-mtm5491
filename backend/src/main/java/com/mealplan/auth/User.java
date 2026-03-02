package com.mealplan.auth;

import java.time.Instant;

/**
 * Local application user
 * - stores ID, providerID, email, timestamps.
 */
public class User {
    String id;          // local user id
    String providerId;  // sub from Google
    String email;
    Instant createdAt;
    Instant updatedAt;

    /**
     * Full constructor for user
     * @param id
     * @param providerId
     * @param email
     * @param createdAt
     * @param updatedAt
     */
    public User(String id, String providerId, String email, Instant createdAt, Instant updatedAt) {
            this.id = id;
            this.providerId = providerId;
            this.email = email;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
    }

    /**
     * empty constructor
     */
    public User() {   
    }

    /**
     * Get User ID
     * @return ID
     */
    public String getId() {
        return id;
    }

    /**
     * set User ID
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get Provider ID
     * @return Provider ID
     */
    public String getProviderId() {
        return providerId;
    }

    /**
     * Set Provider ID
     * @param providerId
     */
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    /**
     * Get email
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set email
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get created at instant
     * @return instant
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Set created at instant
     * @param createdAt
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get updated at instant
     * @return updated instant
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Set updated at instant
     * @param updatedAt
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    
}
