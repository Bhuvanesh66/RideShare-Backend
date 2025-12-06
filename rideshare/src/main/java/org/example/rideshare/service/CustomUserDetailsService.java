package org.example.rideshare.service;

import org.example.rideshare.model.User;
import org.example.rideshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * CustomUserDetailsService - Spring Security integration for user
 * authentication
 * 
 * This service implements Spring Security's UserDetailsService interface and
 * loads
 * user authentication details from the database. It's used during the
 * authentication
 * process to verify user credentials and load user authority information.
 * 
 * The service retrieves User entities from MongoDB and converts them to Spring
 * Security UserDetails objects that include username, password hash, and
 * authorities.
 * 
 * Used by: Spring Security's AuthenticationProvider during login
 * 
 * @author RideShare Development Team
 * @version 1.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

        // Injected UserRepository for database access
        @Autowired
        private UserRepository userRepository;

        /**
         * Load user authentication details by username
         * 
         * This method is called by Spring Security during the authentication process.
         * It loads the user from the database and creates a Spring Security UserDetails
         * object containing the username, BCrypt-hashed password, and user role.
         * 
         * @param username The username to load
         * @return UserDetails object containing authentication information
         * @throws UsernameNotFoundException if user not found in database
         */
        @Override
        public UserDetails loadUserByUsername(String username)
                        throws UsernameNotFoundException {

                // Retrieve user from database, throw exception if not found
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

                // Create and return Spring Security UserDetails
                return new org.springframework.security.core.userdetails.User(
                                user.getUsername(), // Username
                                user.getPassword(), // BCrypt-hashed password
                                Collections.singleton(() -> user.getRole())); // Authority (role)
        }
}
