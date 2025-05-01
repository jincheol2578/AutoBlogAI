package com.autoblog.autoblog.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autoblog.autoblog.api.OpenRouterApiClient;
import com.autoblog.autoblog.domain.DraftReview;
import com.autoblog.autoblog.domain.Product;
import com.autoblog.autoblog.dto.ProductDto;
import com.autoblog.autoblog.repository.DraftReviewRepository;
import com.autoblog.autoblog.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DraftReviewService {

    private final DraftReviewRepository draftReviewRepository;
    private final OpenRouterApiClient deepSeekApiClient;
    private final ProductRepository productRepository;
    
    @Transactional
    public void createDraftReview(ProductDto productDto) {
        System.out.println(productDto);
        // DeepSeek API 호출
        Mono<Map<String, Object>> apiResponse = deepSeekApiClient.getChatResponse(productDto.getProductName(), productDto.getKeywordName());
        // 비동기 처리를 기다림
        Map<String, Object> responseMap = apiResponse.block();
        
        // 응답 데이터 처리
        String title = (String) responseMap.get("title");
        String content = (String) responseMap.get("content");

        // Product 엔티티 조회
        Product product = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));  


        // DraftReview 엔티티 생성 및 저장
        DraftReview draftReview = new DraftReview();
        draftReview.setProduct(product);
        draftReview.setTitle(title);
        draftReview.setContent(content);
        draftReviewRepository.save(draftReview);
    }

    public DraftReview getDraftReview(Long productId) {

        return draftReviewRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Draft review not found for product ID: " + productId));
    }

}
