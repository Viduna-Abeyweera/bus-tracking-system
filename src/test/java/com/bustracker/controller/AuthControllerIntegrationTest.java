package com.bustracker.controller;

import com.bustracker.dto.request.LoginRequest;
import com.bustracker.dto.request.RegisterRequest;
import com.bustracker.dto.response.AuthResponse;
import com.bustracker.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link AuthController}.
 *
 * <p>Tests the full HTTP request lifecycle: request → controller →
 * service (mocked) → response serialization. Verifies status codes,
 * response body, and content types.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/register - should return 201 with JWT")
    void register_Success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Kamal Perera");
        request.setEmail("kamal@example.com");
        request.setPassword("password123");
        request.setPhone("+94771234567");

        AuthResponse mockResponse = new AuthResponse(
                "mock-jwt-token", 1L, "Kamal Perera", "kamal@example.com", "PASSENGER");

        when(authService.register(any(RegisterRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("mock-jwt-token"))
                .andExpect(jsonPath("$.name").value("Kamal Perera"))
                .andExpect(jsonPath("$.role").value("PASSENGER"));
    }

    @Test
    @DisplayName("POST /api/auth/login - should return 200 with JWT")
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("kamal@example.com");
        request.setPassword("password123");

        AuthResponse mockResponse = new AuthResponse(
                "login-jwt-token", 1L, "Kamal Perera", "kamal@example.com", "PASSENGER");

        when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("login-jwt-token"))
                .andExpect(jsonPath("$.name").value("Kamal Perera"));
    }

    @Test
    @DisplayName("POST /api/auth/register - should reject empty body")
    void register_InvalidRequest() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - should reject empty body")
    void login_InvalidRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
