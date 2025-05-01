package com.autoblog.autoblog.dto;

import java.util.List;

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
public class KeywordDto {
    private Long id;
    private String keyword;
    private boolean isValid;
    private String score;
    private List<ProductDto> products;
}
