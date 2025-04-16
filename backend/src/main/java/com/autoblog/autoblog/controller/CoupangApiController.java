package com.autoblog.autoblog.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoblog.autoblog.api.CoupangApiClient;
import com.autoblog.autoblog.dto.CoupangDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cupang")
@RequiredArgsConstructor
public class CoupangApiController {
    
    @PostMapping("/getProduct")
    public ResponseEntity<String> getCupangProduct(@RequestBody CoupangDto dto) {
        try {
        // Call the Coupang API client to get product data
        CoupangApiClient cupangApiClient = new CoupangApiClient();
        cupangApiClient.getCoupangProduct();

        return ResponseEntity.ok("Coupang API 호출 성공");
        } catch (IllegalArgumentException e) {
            // 비즈니스 로직 오류
            return ResponseEntity.badRequest().body("잘못된 요청입니다: " + e.getMessage());
        } catch (IOException e) {
            // 외부 API 통신 오류
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Coupang API 호출 실패: " + e.getMessage());
        } catch (Exception e) {
            // 기타 예외
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
        }
    }

}
