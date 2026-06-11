package com.bustracker.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JwtTokenProvider}.
 *
 * <p>Tests token generation, email/role extraction, and validation
 * including expired and tampered tokens.</p>
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        // Create a test secret key (Base64-encoded, 256-bit)
        String testSecret = Base64.getEncoder().encodeToString(
                "this is a very long secret key for bus tracking jwt token generation".getBytes()
        );
        // 1 hour expiration for tests
        long testExpirationMs = 3600000;

        jwtTokenProvider = new JwtTokenProvider(testSecret, testExpirationMs);
    }

    @Test
    @DisplayName("Should generate a non-null JWT token")
    void testGenerateToken() {
        String token = jwtTokenProvider.generateToken("test@example.com", "PASSENGER");

        assertNotNull(token);
        assertFalse(token.isEmpty());
        // JWT format: header.payload.signature (3 parts separated by dots)
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    @DisplayName("Should extract email from token")
    void testGetEmailFromToken() {
        String email = "driver@bustracker.lk";
        String token = jwtTokenProvider.generateToken(email, "DRIVER");

        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    @DisplayName("Should extract role from token")
    void testGetRoleFromToken() {
        String token = jwtTokenProvider.generateToken("admin@bustracker.lk", "ADMIN");

        String extractedRole = jwtTokenProvider.getRoleFromToken(token);

        assertEquals("ADMIN", extractedRole);
    }

    @Test
    @DisplayName("Should validate a valid token")
    void testValidateValidToken() {
        String token = jwtTokenProvider.generateToken("test@example.com", "PASSENGER");

        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Should reject an invalid token")
    void testValidateInvalidToken() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.here"));
    }

    @Test
    @DisplayName("Should reject a null token")
    void testValidateNullToken() {
        assertFalse(jwtTokenProvider.validateToken(null));
    }

    @Test
    @DisplayName("Should reject an expired token")
    void testValidateExpiredToken() {
        // Create a provider with 0ms expiration (token expires immediately)
        String testSecret = Base64.getEncoder().encodeToString(
                "this is a very long secret key for bus tracking jwt token generation".getBytes()
        );
        JwtTokenProvider expiredProvider = new JwtTokenProvider(testSecret, 0);

        String token = expiredProvider.generateToken("test@example.com", "PASSENGER");

        // Token should be expired immediately
        assertFalse(expiredProvider.validateToken(token));
    }

    @Test
    @DisplayName("Should reject a tampered token")
    void testValidateTamperedToken() {
        String token = jwtTokenProvider.generateToken("test@example.com", "PASSENGER");

        // Tamper with the token by modifying the payload
        String[] parts = token.split("\\.");
        String tamperedToken = parts[0] + "." + parts[1] + "x" + "." + parts[2];

        assertFalse(jwtTokenProvider.validateToken(tamperedToken));
    }
}
