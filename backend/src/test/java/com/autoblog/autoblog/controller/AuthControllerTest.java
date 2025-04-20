package com.autoblog.autoblog.controller;

import com.autoblog.autoblog.dto.JwtResponseDto;
import com.autoblog.autoblog.dto.LoginDto;
import com.autoblog.autoblog.dto.RegisterDto;
import com.autoblog.autoblog.service.AuthService;
import com.autoblog.autoblog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void testRegisterSuccess() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("testuser");
        registerDto.setEmail("testuser@example.com");
        registerDto.setPassword("password");

        Mockito.doNothing().when(userService).registerUser(Mockito.any(RegisterDto.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully."));
    }

    @Test
    @DisplayName("회원가입 실패 테스트")
    void testRegisterFailure() throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername("testuser");
        registerDto.setEmail("testuser@example.com");
        registerDto.setPassword("password");

        Mockito.doThrow(new RuntimeException("User already exists"))
                .when(userService).registerUser(Mockito.any(RegisterDto.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User already exists"));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void testLoginSuccess() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("password");

        String token = "mock-jwt-token";
        Mockito.when(authService.login(Mockito.any(LoginDto.class))).thenReturn(token);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    void testLoginFailure() throws Exception {
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("wrongpassword");

        Mockito.doThrow(new RuntimeException("Invalid credentials"))
                .when(authService).login(Mockito.any(LoginDto.class));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials."));
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void testLogoutSuccess() throws Exception {
        Mockito.doNothing().when(authService).logout();

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("User logged out successfully."));
    }
}
