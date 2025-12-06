package org.example.rideshare.repository;

import org.example.rideshare.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * UserRepository - Data access layer for User entities
 * 
 * This Spring Data MongoDB repository provides database operations for User
 * entities.
 * It extends MongoRepository which provides CRUD operations (Create, Read,
 * Update, Delete)
 * automatically. Custom query methods can be defined as needed.
 * 
 * Inherited Methods (from MongoRepository):
 * - save(User) - Create or update a user
 * - findById(String) - Get user by ID
 * - findAll() - Get all users
 * - delete(User) - Delete a user
 * - deleteById(String) - Delete user by ID
 * 
 * Custom Methods:
 * - findByUsername(String) - Find user by unique username
 * 
 * Used by: AuthService for user lookups
 */
public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Find a user by their username
     * 
     * Since username is unique (enforced by MongoDB index), this returns
     * an Optional containing either the user if found, or empty if not found.
     * 
     * @param username The username to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByUsername(String username);
}
