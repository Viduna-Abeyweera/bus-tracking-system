package com.bustracker.service;

import com.bustracker.dto.request.LoginRequest;
import com.bustracker.dto.request.RegisterRequest;
import com.bustracker.dto.response.AuthResponse;

/**
 * Service interface for authentication operations.
 *
 * <p>Follows the <strong>Dependency Inversion Principle (DIP)</strong>:
 * the controller depends on this abstraction, not the concrete
 * implementation. This allows easy swapping of auth strategies
 * (e.g., OAuth2) without changing the controller.</p>
 */
public interface AuthService {

    /**
     * Registers a new user in the system.
     *
     * @param request the validated registration request
     * @return an auth response with JWT token and user info
     * @throws com.bustracker.exception.BadRequestException if email already exists
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates a user with email and password.
     *
     * @param request the validated login request
     * @return an auth response with JWT token and user info
     * @throws org.springframework.security.authentication.BadCredentialsException if credentials are invalid
     */
    AuthResponse login(LoginRequest request);
}
