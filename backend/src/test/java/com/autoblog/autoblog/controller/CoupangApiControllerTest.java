package com.autoblog.autoblog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

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

import com.autoblog.autoblog.service.ProductService;

@SpringBootTest
@AutoConfigureMockMvc
public class CoupangApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService coupangApiService;

    @Test
    @DisplayName("Coupang 상품 검색 성공 테스트")
    @WithMockUser(username = "testuser")
    void testGetCoupangProductSuccess() throws Exception {
        // Mock 데이터 설정
        String keyword = "밀레청소기";
        String mockResponse = "{\"products\": [{\"name\": \"밀레 청소기\", \"price\": 100000}]}";

        Mockito.when(coupangApiService.getCoupangProducts(Mockito.eq(keyword), Mockito.eq("testuser")))
                .thenReturn(mockResponse);

        // API 호출 및 검증
        mockMvc.perform(post("/api/cupang/getProduct")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    @DisplayName("Coupang 상품 검색 실패 테스트 - 잘못된 요청")
    @WithMockUser(username = "testuser1")
    void testGetCoupangProductBadRequest() throws Exception {
        // Mock 데이터 설정
        String keyword = "밀레청소기";

        Mockito.when(coupangApiService.getCoupangProducts(Mockito.eq(keyword), Mockito.eq("testuser1")))
                .thenThrow(new IllegalArgumentException("잘못된 요청입니다."));

        // API 호출 및 검증
        mockMvc.perform(post("/api/cupang/getProduct")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("잘못된 요청입니다: 잘못된 요청입니다."));
    }

    @Test
    @DisplayName("Coupang 상품 검색 실패 테스트 - API 호출 실패")
    @WithMockUser(username = "testuser")
    void testGetCoupangProductApiFailure() throws Exception {
        // Mock 데이터 설정
        String keyword = "밀레청소기";

        Mockito.when(coupangApiService.getCoupangProducts(Mockito.eq(keyword), Mockito.eq("testuser")))
                .thenThrow(new IOException("Coupang API 호출 실패"));

        // API 호출 및 검증
        mockMvc.perform(post("/api/cupang/getProduct")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string("Coupang API 호출 실패: Coupang API 호출 실패"));
    }
}