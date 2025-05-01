package com.autoblog.autoblog.controller;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.autoblog.autoblog.dto.ApiKeyDto;
import com.autoblog.autoblog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("Coupang 정보 업데이트 성공 테스트")
    @WithMockUser(username = "mockuser")
    void testUpdateCoupangInfoSuccess() throws Exception {
        // Mock 데이터 설정
        ApiKeyDto dto = new ApiKeyDto();
        dto.setSubId("mockSubld");
        dto.setApiKey("mockApiKey");
        dto.setSecretKey("mockSecretKey");

        Mockito.doNothing().when(userService).updateUserInfo(Mockito.eq("mockuser"), Mockito.any(ApiKeyDto.class));

        // API 호출 및 검증
        mockMvc.perform(put("/api/user/coupang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupang 정보가 업데이트되었습니다."));
    }

    @Test
    @DisplayName("Coupang 정보 업데이트 실패 테스트 - 잘못된 입력")
    @WithMockUser(username = "mockuser")
    void testUpdateCoupangInfoBadRequest() throws Exception {
        // Mock 데이터 설정 (필수 필드 누락)
        ApiKeyDto dto = new ApiKeyDto();
        dto.setApiKey("mockApiKey");
        // secretKey 누락

        // API 호출 및 검증
        mockMvc.perform(put("/api/user/coupang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Coupang 정보 업데이트 실패 테스트 - 서비스 예외 발생")
    @WithMockUser(username = "mockuser")
    void testUpdateCoupangInfoServiceException() throws Exception {
        // Mock 데이터 설정
        ApiKeyDto dto = new ApiKeyDto();
        dto.setSubId("mockSubld");
        dto.setApiKey("mockApiKey");
        dto.setSecretKey("mockSecretKey");

        Mockito.doThrow(new RuntimeException("Mock 서비스 오류 발생"))
                .when(userService).updateUserInfo(Mockito.eq("mockuser"), Mockito.any(ApiKeyDto.class));

        // API 호출 및 검증
        mockMvc.perform(put("/api/user/coupang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Mock 서비스 오류 발생"));
    }
}