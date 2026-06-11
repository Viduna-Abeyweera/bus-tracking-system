package com.bustracker.service.impl;

import com.bustracker.dto.request.LoginRequest;
import com.bustracker.dto.request.RegisterRequest;
import com.bustracker.dto.response.AuthResponse;
import com.bustracker.entity.User;
import com.bustracker.enums.UserRole;
import com.bustracker.exception.BadRequestException;
import com.bustracker.repository.UserRepository;
import com.bustracker.security.JwtTokenProvider;
import com.bustracker.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link AuthService} providing JWT-based authentication.
 *
 * <p>This class demonstrates several OOP and SOLID principles:</p>
 * <ul>
 *   <li><strong>SRP</strong>: Only handles authentication logic (register + login).
 *       JWT generation is delegated to {@code JwtTokenProvider}, password
 *       hashing to {@code PasswordEncoder}, and authentication verification
 *       to {@code AuthenticationManager}.</li>
 *   <li><strong>DIP</strong>: Depends on interfaces ({@code AuthService},
 *       {@code PasswordEncoder}, {@code AuthenticationManager}).</li>
 *   <li><strong>Encapsulation</strong>: Password is hashed before storage
 *       and never returned in responses.</li>
 * </ul>
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email '" + request.getEmail() + "' is already registered");
        }

        // Determine user role (default to PASSENGER)
        UserRole role = UserRole.PASSENGER;
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                role = UserRole.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException(
                        "Invalid role: '" + request.getRole() + "'. Valid roles: PASSENGER, DRIVER, ADMIN");
            }
        }

        // Create user entity with hashed password
        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getPhone(),
                role
        );

        User savedUser = userRepository.save(user);
        logger.info("New user registered: {} ({})", savedUser.getEmail(), savedUser.getRole());

        // Generate JWT token for the new user
        String token = jwtTokenProvider.generateToken(
                savedUser.getEmail(),
                savedUser.getRole().name()
        );

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate using Spring Security's AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (BadCredentialsException e) {
            logger.warn("Failed login attempt for email: {}", request.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }

        // Load user from database for response
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        logger.info("User logged in: {} ({})", user.getEmail(), user.getRole());

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
