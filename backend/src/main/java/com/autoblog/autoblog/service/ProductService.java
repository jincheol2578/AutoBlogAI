package com.autoblog.autoblog.service;

import java.io.IOException;
import java.security.Key;

import org.springframework.stereotype.Service;

import com.autoblog.autoblog.api.CoupangApiClient;
import com.autoblog.autoblog.domain.Product;
import com.autoblog.autoblog.domain.User;
import com.autoblog.autoblog.domain.Keyword;
import com.autoblog.autoblog.dto.ApiKeyDto;
import com.autoblog.autoblog.dto.ProductDto;
import com.autoblog.autoblog.repository.ProductRepository;
import com.autoblog.autoblog.repository.UserRepository;
import com.autoblog.autoblog.repository.KeywordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;
    private final ProductRepository productRepository;
    private final CoupangApiClient coupangApiClient;

    public String getCoupangProducts(String keyword, String username) throws IOException {
        // 데이터베이스에서 사용자 정보 가져오기
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 사용자 정보를 기반으로 CoupangApiDto 생성
        ApiKeyDto apiDto = new ApiKeyDto();
        apiDto.setApiKey(user.getCoupangApiKey());
        apiDto.setSecretKey(user.getCoupangSecretKey());
        apiDto.setKeyword(keyword);

        // CoupangApiClient 호출
        return coupangApiClient.getCoupangProduct(apiDto);
    }

    public String addProduct(ProductDto productDto, String username) {
        try {
            // 사용자 정보 가져오기
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            // 키워드 ID로 키워드 조회
            Keyword keyword = keywordRepository.findById(productDto.getKeywordId())
                    .orElseThrow(() -> new IllegalArgumentException("키워드를 찾을 수 없습니다."));

            // Product 엔티티 생성
            Product product = Product.builder()
                    .keyword(keyword) 
                    .productName(productDto.getProductName())
                    .price(productDto.getPrice())
                    .images(null) // TODO: 이미지 URL 처리 필요
                    .description(null) // TODO: 필요할까?
                    .productUrl(productDto.getProductUrl())
                    .build();

            // 데이터베이스에 저장
            productRepository.save(product);

            return "Product added successfully";
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to add product: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while adding the product", e);
        }
    }
}