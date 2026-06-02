package com.bustracker.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * Utility component for creating and validating JWT (JSON Web Token) tokens.
 *
 * <p>This class is responsible for:</p>
 * <ul>
 *   <li>Generating a signed JWT token after successful authentication</li>
 *   <li>Extracting the username (email) from a token</li>
 *   <li>Validating that a token is not expired or tampered with</li>
 * </ul>
 *
 * <p>The JWT secret and expiration time are externalized to
 * {@code application.properties} to follow security best practices.</p>
 *
 * <p>This class follows the <strong>Single Responsibility Principle</strong>:
 * it handles only JWT operations — authentication logic resides in
 * {@code AuthService}, and HTTP filter logic in
 * {@code JwtAuthenticationFilter}.</p>
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey secretKey;
    private final long expirationMs;

    /**
     * Constructor that initializes the signing key from configuration.
     *
     * @param jwtSecret     Base64-encoded secret key from application.properties
     * @param expirationMs  token expiration time in milliseconds
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
        this.expirationMs = expirationMs;
    }

    /**
     * Generates a JWT token for an authenticated user.
     *
     * @param email the user's email (used as the subject claim)
     * @param role  the user's role (stored as a custom claim)
     * @return a signed JWT token string
     */
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extracts the email (subject) from a JWT token.
     *
     * @param token the JWT token
     * @return the email stored in the token's subject claim
     */
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Extracts the role from a JWT token.
     *
     * @param token the JWT token
     * @return the role stored in the token's custom claim
     */
    public String getRoleFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    /**
     * Validates a JWT token for integrity and expiration.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            logger.warn("JWT token expired: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.warn("Invalid JWT token: {}", ex.getMessage());
        } catch (JwtException ex) {
            logger.warn("JWT validation failed: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.warn("JWT claims string is empty: {}", ex.getMessage());
        }
        return false;
    }
}
