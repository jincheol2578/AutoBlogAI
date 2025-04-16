package com.autoblog.autoblog.controller;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.autoblog.autoblog.dto.CoupangDto;
import com.autoblog.autoblog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;


// filepath: backend/src/test/java/com/autoblog/autoblog/controller/UserControllerTest.java




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
    @WithMockUser(username = "testuser", roles = {"USER"}) // 필요한 역할 추가
    void testUpdateCoupangInfoSuccess() throws Exception {
        CoupangDto coupangDto = new CoupangDto();
        coupangDto.setSubld("subldValue");
        coupangDto.setApiKey("apiKeyValue");
        coupangDto.setSecretKey("secretKeyValue");

        Mockito.doNothing().when(userService).updateCoupangInfo(Mockito.eq("testuser"), Mockito.any(CoupangDto.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/coupang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coupangDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupang 정보가 업데이트되었습니다."));
    }

    @Test
    @DisplayName("Coupang 정보 업데이트 실패 테스트 - 잘못된 입력")
    @WithMockUser(username = "testuser")
    void testUpdateCoupangInfoBadRequest() throws Exception {
        CoupangDto coupangDto = new CoupangDto(); // 필수 필드가 비어 있음

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/coupang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coupangDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Subld는 비어 있을 수 없습니다.")); // 유효성 검사 메시지
    }

    @Test
    @DisplayName("Coupang 정보 업데이트 실패 테스트 - UserService 예외 발생")
    @WithMockUser(username = "testuser")
    void testUpdateCoupangInfoServiceException() throws Exception {
        CoupangDto coupangDto = new CoupangDto();
        coupangDto.setSubld("validSubld");
        coupangDto.setApiKey("apiKeyValue");
        coupangDto.setSecretKey("secretKeyValue");

        Mockito.doThrow(new IllegalArgumentException("Invalid data"))
                .when(userService).updateCoupangInfo(Mockito.eq("testuser"), Mockito.any(CoupangDto.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/coupang")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coupangDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid data")); // UserService 예외 메시지
    }



}