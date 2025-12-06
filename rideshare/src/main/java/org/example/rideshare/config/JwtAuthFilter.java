package org.example.rideshare.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.rideshare.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthFilter - Spring Security filter for JWT token validation
 * 
 * This filter intercepts every HTTP request and processes JWT bearer tokens
 * from the Authorization header. It validates tokens and establishes the
 * authentication context for the request if the token is valid.
 * 
 * Request Flow:
 * 1. Extract "Bearer {token}" from Authorization header
 * 2. Validate token signature and expiration using JwtUtil
 * 3. Extract username from valid token
 * 4. Load user details from database
 * 5. Create Spring Security authentication token
 * 6. Store authentication in SecurityContext
 * 
 * Security Filter Behavior:
 * - Runs once per request (extends OncePerRequestFilter)
 * - Only processes Authorization headers with "Bearer " prefix
 * - Validates tokens before setting authentication
 * - Allows unauthenticated requests to proceed (handled by @PreAuthorize)
 * 
 * Used by: Spring Security filter chain for request processing
 * 
 * @author RideShare Development Team
 * @version 1.0
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    // JWT utility for token operations
    @Autowired
    private JwtUtil jwtUtil;

    // Custom user details service for loading user information
    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Process HTTP request and validate JWT token if present
     * 
     * This method is called for each HTTP request. It:
     * 1. Extracts JWT token from Authorization header (Bearer token)
     * 2. Validates token signature and expiration
     * 3. Loads user details if token is valid
     * 4. Sets Spring Security authentication context
     * 5. Proceeds with filter chain
     * 
     * @param request     The HTTP servlet request
     * @param response    The HTTP servlet response
     * @param filterChain The filter chain to proceed through
     * @throws ServletException If an error occurs in request processing
     * @throws IOException      If an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Get Authorization header from request
        String header = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Extract token from "Bearer {token}" format
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            token = header.substring(7);
            // Validate token signature and expiration
            if (jwtUtil.validateToken(token)) {
                // Extract username from valid token
                username = jwtUtil.getUsername(token);
            }
        }

        // Set authentication context if token is valid and not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Create Spring Security authentication token with user details and authorities
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // Set additional request details
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Store authentication in SecurityContext for this request
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // Proceed with the filter chain
        filterChain.doFilter(request, response);
    }
}
