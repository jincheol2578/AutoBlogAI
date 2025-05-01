package com.autoblog.autoblog.dto;

import java.time.LocalDateTime;

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
public class DraftReviewDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    
}
