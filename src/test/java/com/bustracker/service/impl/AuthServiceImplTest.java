package com.bustracker.service.impl;

import com.bustracker.dto.request.LoginRequest;
import com.bustracker.dto.request.RegisterRequest;
import com.bustracker.dto.response.AuthResponse;
import com.bustracker.entity.User;
import com.bustracker.enums.UserRole;
import com.bustracker.exception.BadRequestException;
import com.bustracker.repository.UserRepository;
import com.bustracker.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AuthServiceImpl}.
 *
 * <p>Tests registration (including duplicate email), login (including
 * invalid credentials), and role validation using Mockito mocks.</p>
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "Kamal Perera", "kamal@example.com", "password123", "0771234567", "PASSENGER"
        );

        loginRequest = new LoginRequest("kamal@example.com", "password123");

        sampleUser = new User(
                "Kamal Perera", "kamal@example.com", "hashedPassword", "0771234567", UserRole.PASSENGER
        );
        sampleUser.setId(1L);
    }

    @Test
    @DisplayName("Should register a new user and return JWT token")
    void testRegisterSuccess() {
        when(userRepository.existsByEmail("kamal@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtTokenProvider.generateToken("kamal@example.com", "PASSENGER"))
                .thenReturn("mock.jwt.token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getAccessToken());
        assertEquals("Kamal Perera", response.getName());
        assertEquals("kamal@example.com", response.getEmail());
        assertEquals("PASSENGER", response.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException for duplicate email")
    void testRegisterDuplicateEmail() {
        when(userRepository.existsByEmail("kamal@example.com")).thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authService.register(registerRequest)
        );

        assertTrue(exception.getMessage().contains("already registered"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BadRequestException for invalid role")
    void testRegisterInvalidRole() {
        RegisterRequest invalidRequest = new RegisterRequest(
                "Test User", "test@example.com", "password123", null, "INVALID_ROLE"
        );
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> authService.register(invalidRequest)
        );

        assertTrue(exception.getMessage().contains("Invalid role"));
    }

    @Test
    @DisplayName("Should default to PASSENGER role when no role specified")
    void testRegisterDefaultRole() {
        RegisterRequest noRoleRequest = new RegisterRequest(
                "Test User", "test@example.com", "password123", null, null
        );

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        when(jwtTokenProvider.generateToken(anyString(), anyString())).thenReturn("token");

        AuthResponse response = authService.register(noRoleRequest);

        assertNotNull(response);
        assertEquals("PASSENGER", response.getRole());
    }

    @Test
    @DisplayName("Should login and return JWT token")
    void testLoginSuccess() {
        Authentication mockAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(userRepository.findByEmail("kamal@example.com")).thenReturn(Optional.of(sampleUser));
        when(jwtTokenProvider.generateToken("kamal@example.com", "PASSENGER"))
                .thenReturn("mock.jwt.token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getAccessToken());
        assertEquals("kamal@example.com", response.getEmail());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException for invalid password")
    void testLoginInvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest));
    }
}
