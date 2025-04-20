package com.autoblog.autoblog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autoblog.autoblog.dto.ApiKeyDto;
import com.autoblog.autoblog.service.UserService;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/coupang")
    public ResponseEntity<?> updateCoupangInfo(
            @Valid @RequestBody ApiKeyDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            userService.updateCoupangInfo(userDetails.getUsername(), dto);
            return ResponseEntity.ok("Coupang 정보가 업데이트되었습니다.");
        } catch (RuntimeException e) {
            // JSON 객체로 에러 메시지 반환
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}