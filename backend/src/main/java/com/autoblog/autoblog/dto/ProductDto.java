package com.autoblog.autoblog.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductDto {
    private Long id;
    private Long keywordId;
    private String productName;
    private Long price;
    private String description;
    private String productUrl;
    private String partnersUrl;
    private String imageUrl;
    private String keywordName;
    private List<DraftReviewDto> draftReviews;
    private List<ProductImageDto> productImage;
}