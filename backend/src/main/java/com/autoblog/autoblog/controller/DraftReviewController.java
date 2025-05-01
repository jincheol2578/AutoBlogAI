package com.autoblog.autoblog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoblog.autoblog.dto.ProductDto;
import com.autoblog.autoblog.service.DraftReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/draft-review")
@RequiredArgsConstructor
public class DraftReviewController {
    
    private final DraftReviewService draftReviewService;
    
    @PostMapping("/generate")
    public ResponseEntity<?> createDraftReview(@RequestBody ProductDto product) {
        try {
            draftReviewService.createDraftReview(product);
            return ResponseEntity.ok("Draft review created successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error creating draft review: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDraftReview(@PathVariable Long id) {
        try {
            draftReviewService.getDraftReview(id);
            return ResponseEntity.ok("Draft reviews retrieved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }
}
