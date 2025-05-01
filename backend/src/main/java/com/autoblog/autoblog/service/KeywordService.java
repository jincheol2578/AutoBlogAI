package com.autoblog.autoblog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autoblog.autoblog.domain.Keyword;
import com.autoblog.autoblog.domain.User;
import com.autoblog.autoblog.dto.DraftReviewDto;
import com.autoblog.autoblog.dto.KeywordDto;
import com.autoblog.autoblog.dto.ProductDto;
import com.autoblog.autoblog.dto.ProductImageDto;
import com.autoblog.autoblog.repository.KeywordRepository;
import com.autoblog.autoblog.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final UserRepository userRepository;

    public void addKeyword(String username, String keyword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        boolean exists = keywordRepository.existsByKeywordAndUser(keyword, user);

        if (exists) {
            throw new IllegalArgumentException("이미 저장된 키워드입니다.");
        }

        Keyword newKeyword = new Keyword();
        newKeyword.setKeyword(keyword);
        newKeyword.setUser(user);

        keywordRepository.save(newKeyword);
    }

    public List<KeywordDto> getKeyword(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Keyword> keywords = keywordRepository.findByUser(user);

        return keywords.stream()
                .map(keyword -> new KeywordDto(keyword.getId(), keyword.getKeyword(), keyword.getIsValid(),
                        keyword.getScore(), new ArrayList<>()))
                .toList();

    }

    @Transactional(readOnly = true)
public List<KeywordDto> getKeywordsAndProducts(String username) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    List<Keyword> keywords = keywordRepository.findByUserWithProducts(user);

    return keywords.stream()
            .map(keyword -> {
                List<ProductDto> productDtos = keyword.getProducts().stream()
                        .map(product -> {
                            List<DraftReviewDto> draftReviewDtos = product.getDraftReviews().stream()
                                    .map(draftReview -> new DraftReviewDto(
                                            draftReview.getId(),
                                            draftReview.getTitle(),
                                            draftReview.getContent(),
                                            draftReview.getCreatedAt()))
                                    .collect(Collectors.toList());

                            List<ProductImageDto> productImageDtos = product.getProductImages().stream()
                                    .map(image -> new ProductImageDto(
                                            image.getId(),
                                            image.getImageUrl(),
                                            image.isMain()))
                                    .collect(Collectors.toList());

                            return new ProductDto(
                                    product.getId(),
                                    keyword.getId(),
                                    product.getProductName(),
                                    product.getPrice(),
                                    product.getDescription(),
                                    product.getProductUrl(),
                                    product.getPartnerUrl(),
                                    productImageDtos.isEmpty() ? null : productImageDtos.get(0).getImageUrl(), // 대표 이미지
                                    keyword.getKeyword(),
                                    draftReviewDtos,
                                    productImageDtos // 전체 이미지 리스트
                            );
                        })
                        .collect(Collectors.toList());

                return new KeywordDto(
                        keyword.getId(),
                        keyword.getKeyword(),
                        keyword.getIsValid(),
                        keyword.getScore(),
                        productDtos
                );
            })
            .collect(Collectors.toList());
}

    public void deleteKeyword(String username, String[] keywords) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Keyword> existingKeywords = keywordRepository.findByKeywordInAndUser(keywords, user);

        if (existingKeywords.isEmpty()) {
            throw new IllegalArgumentException("삭제할 키워드가 없습니다.");
        }

        keywordRepository.deleteAllInBatch(existingKeywords);
    }
}
