package org.example.rideshare.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.Objects;

/**
 * User - Entity model representing a RideShare platform user
 * 
 * This is the primary user entity stored in MongoDB. It represents both
 * regular users (passengers) and drivers on the platform. All user data
 * is persisted in the "users" collection.
 * 
 * Roles:
 * - ROLE_USER: Regular passenger who can request rides
 * - ROLE_DRIVER: Driver who can accept and complete rides
 * 
 * Security:
 * - Password is stored as a BCrypt hash (never stored in plaintext)
 * - Username is unique across the system (enforced by MongoDB unique index)
 * - Used in Spring Security for authentication and authorization
 * 
 * @author RideShare Development Team
 * @version 1.0
 */
@Document(collection = "users")
public class User {

    // MongoDB document ID - automatically generated
    @Id
    private String id;

    // Unique username for login (enforced with MongoDB unique index)
    @Indexed(unique = true)
    private String username;

    // BCrypt-hashed password for authentication
    private String password;

    // User role: ROLE_USER (passenger) or ROLE_DRIVER (driver)
    private String role;

    // Default constructor for MongoDB and frameworks
    public User() {
    }

    // Constructor to initialize all fields
    public User(String id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter for document ID
    public String getId() {
        return id;
    }

    // Setter for document ID
    public void setId(String id) {
        this.id = id;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Setter for password
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter for role
    public String getRole() {
        return role;
    }

    // Setter for role
    public void setRole(String role) {
        this.role = role;
    }

    // Equality comparison based on user ID
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    // Hash code based on user ID
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Static factory method to get a User builder
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder - Fluent API for constructing User instances
     * 
     * Provides a clean and readable way to build User objects with optional fields.
     * Example: User user =
     * User.builder().username("john").role("ROLE_USER").build();
     */
    public static class Builder {
        private String id;
        private String username;
        private String password;
        private String role;

        // Set the user ID
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        // Set the username
        public Builder username(String username) {
            this.username = username;
            return this;
        }

        // Set the password
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        // Set the role
        public Builder role(String role) {
            this.role = role;
            return this;
        }

        // Build and return the User object
        public User build() {
            return new User(id, username, password, role);
        }
    }
}
