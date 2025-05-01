package com.autoblog.autoblog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoblog.autoblog.dto.KeywordDto;
import com.autoblog.autoblog.service.KeywordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/keywords")
@RequiredArgsConstructor
public class KeywordController {
    
    private final KeywordService keywordService;
    
    @PostMapping("/generate")
    public ResponseEntity<?> generateKeyword(@AuthenticationPrincipal UserDetails userDetails,
        @RequestBody KeywordDto keywordDto) {
            try {
                System.out.println(keywordDto.toString());
                keywordService.addKeyword(userDetails.getUsername(), keywordDto.getKeyword());
                return ResponseEntity.ok("Keyword added successfully.");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
            } 
            
    }

    @GetMapping
    public ResponseEntity<?> getKeyword(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(keywordService.getKeyword(userDetails.getUsername()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteKeyword(@AuthenticationPrincipal UserDetails userDetails,
        @RequestBody String[] keyword) {
            try {
                keywordService.deleteKeyword(userDetails.getUsername(), keyword);
                return ResponseEntity.ok("Keyword deleted successfully.");
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
            }
    }

    @GetMapping("/products")
    public ResponseEntity<?> getKeywordsAndProducts(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(keywordService.getKeywordsAndProducts(userDetails.getUsername()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        }
    }
}
