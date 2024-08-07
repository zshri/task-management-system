package org.example.taskmanagementsystem;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.example.taskmanagementsystem.security.AuthRequest;
import org.example.taskmanagementsystem.security.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DisplayName("Auth Controller Integration Tests")
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Transactional
    public void testAuthenticate_Success() throws Exception {

        AuthRequest authRequest = AuthRequest.builder()
                .email("jo22hrn.doe@example.com")
                .password("secret123134")
                .build();

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isNotEmpty());

    }

    @Test
    @Transactional
    public void testAuthenticate_Failed() throws Exception {

        AuthRequest authRequest = AuthRequest.builder()
                .email("jo22hrn.doe@example.com")
                .password("secret12")
                .build();

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Bad credentials")));
    }


    @Test
    @Transactional
    public void testRegistration_Success() throws Exception {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Zshri")
                .email("jo22hrn@example.com")
                .password("secret12")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isNotEmpty());
    }

    @Test
    @Transactional
    public void testRegistration_AlreadyExist() throws Exception {

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Zshri")
                .email("jo22hrn.doe@example.com")
                .password("secret12")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("User already exists")));
    }




}
