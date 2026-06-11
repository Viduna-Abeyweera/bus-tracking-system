package com.bustracker.dto.response;

/**
 * Response DTO returned after successful authentication (login or registration).
 *
 * <p>Contains the JWT access token and basic user information.
 * The frontend stores this token and sends it in the
 * {@code Authorization: Bearer <token>} header for subsequent requests.</p>
 *
 * <p>Note: The password is <strong>never</strong> included in this response.</p>
 */
public class AuthResponse {

    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String name;
    private String email;
    private String role;

    // ===== CONSTRUCTORS =====

    public AuthResponse() {
    }

    public AuthResponse(String accessToken, Long userId, String name,
                        String email, String role) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // ===== GETTERS AND SETTERS =====

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
