package com.autoblog.autoblog.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.autoblog.autoblog.service.CoupangApiService;
import com.autoblog.autoblog.dto.ProductDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cupang")
@RequiredArgsConstructor
public class CoupangApiController {

    private final CoupangApiService coupangApiService;

    @PostMapping("/getProduct")
    public ResponseEntity<?> getCupangProduct(@RequestParam String keyword,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // 서비스 호출
            String response = coupangApiService.getCoupangProducts(keyword, userDetails.getUsername());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Coupang API 호출 실패: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@RequestBody ProductDto productDto,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // 서비스 호출
            coupangApiService.addProduct(productDto, userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body("상품이 성공적으로 등록되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("잘못된 요청입니다: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
        }
    }

}
