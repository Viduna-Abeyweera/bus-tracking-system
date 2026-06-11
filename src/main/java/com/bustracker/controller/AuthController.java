package com.bustracker.controller;

import com.bustracker.dto.request.LoginRequest;
import com.bustracker.dto.request.RegisterRequest;
import com.bustracker.dto.response.AuthResponse;
import com.bustracker.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 *
 * <p>All endpoints under {@code /api/auth/**} are publicly accessible
 * (configured in {@code SecurityConfig}) so that users can register
 * and log in without a JWT token.</p>
 *
 * <p>This controller is intentionally thin — all business logic
 * (validation, hashing, token generation) resides in
 * {@link com.bustracker.service.impl.AuthServiceImpl}.</p>
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration and login endpoints")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user and returns a JWT token.
     *
     * <p>After successful registration, the user is automatically
     * authenticated — no separate login call is needed.</p>
     */
    @PostMapping("/register")
    @Operation(summary = "Register new user",
            description = "Create a new user account. Role defaults to PASSENGER if not specified. Returns JWT token.")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates a user and returns a JWT token.
     */
    @PostMapping("/login")
    @Operation(summary = "Login",
            description = "Authenticate with email and password. Returns JWT token for subsequent API calls.")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
