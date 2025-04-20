package com.autoblog.autoblog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long keywordId;
    private Long userId;
    private String productName;
    private Long price;
    private String description;
    private String productUrl;
    private String partnerUrl;
}