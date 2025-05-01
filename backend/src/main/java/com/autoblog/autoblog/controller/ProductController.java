package com.autoblog.autoblog.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoblog.autoblog.dto.ProductDto;
import com.autoblog.autoblog.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @GetMapping("{id}")
    public ResponseEntity<?> getCupangProduct(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : "anonymous";
        logger.info("GET /api/products/{} called by {}", id, username);

        try {
            List<ProductDto> productList = productService.getCoupangProducts(id, username);
            logger.info("Fetched {} products for keywordId {}", productList.size(), id);
            return ResponseEntity.ok(productList);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for product fetch: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("잘못된 요청입니다: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Coupang API call failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Coupang API 호출 실패: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Server error during product fetch: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<?> addProduct(@RequestBody ProductDto productDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : "anonymous";
        logger.info("POST /api/products/generate called by {} with product: {}", username, productDto);

        try {
            productService.addProduct(productDto, username);
            logger.info("Product added successfully for keywordId {}", productDto.getKeywordId());
            return ResponseEntity.status(HttpStatus.CREATED).body("상품이 성공적으로 등록되었습니다.");
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid product data: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("잘못된 요청입니다: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Server error during product addition: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("/images")
    public ResponseEntity<?> crawlImages(@RequestBody ProductDto productDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            logger.info("이미지 크롤링 시작: 사용자: {}, 키워드: {}", userDetails.getUsername(), productDto.getKeywordName());

            // 이미지 크롤링 및 저장 비동기 처리
            productService.addProductImages(userDetails.getUsername(), productDto);
            // 응답을 즉시 반환
            return ResponseEntity.status(HttpStatus.CREATED).body("이미지가 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            logger.error("이미지 크롤링 요청 처리 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 크롤링 처리 중 오류가 발생했습니다.");
        }
    }

}
