package com.autoblog.autoblog.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autoblog.autoblog.api.DeepSeekApiClient;
import com.autoblog.autoblog.domain.DraftReview;
import com.autoblog.autoblog.domain.Product;
import com.autoblog.autoblog.repository.DraftReviewRepository;
import com.autoblog.autoblog.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DraftReviewService {

    private final DraftReviewRepository draftReviewRepository;
    private final DeepSeekApiClient deepSeekApiClient;
    private final ProductRepository productRepository;
    
    @Transactional
    public DraftReview createDraftReview(Long productId, String productName, String keyword) {
        // DeepSeek API 호출
        Map<String, Object> apiResponse = deepSeekApiClient.getChatResponse(productName, keyword);

        // 응답 데이터 처리
        String title = (String) apiResponse.get("title");
        String content = (String) apiResponse.get("content");

        // Product 엔티티 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));  


        // DraftReview 엔티티 생성 및 저장
        DraftReview draftReview = new DraftReview();
        draftReview.setProduct(product);
        draftReview.setTitle(title);
        draftReview.setContent(content);

        return draftReviewRepository.save(draftReview);
    }
}
