package com.autoblog.autoblog.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.autoblog.autoblog.api.CoupangApiClient;
import com.autoblog.autoblog.domain.User;
import com.autoblog.autoblog.dto.CoupangApiDto;
import com.autoblog.autoblog.dto.ProductDto;
import com.autoblog.autoblog.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoupangApiService {

    private final UserRepository userRepository;
    private final CoupangApiClient coupangApiClient;

    public String getCoupangProducts(String keyword, String username) throws IOException {
        // 데이터베이스에서 사용자 정보 가져오기
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 사용자 정보를 기반으로 CoupangApiDto 생성
        CoupangApiDto apiDto = new CoupangApiDto();
        apiDto.setApiKey(user.getCoupangApiKey());
        apiDto.setSecretKey(user.getCoupangSecretKey());
        apiDto.setKeyword(keyword);

        // CoupangApiClient 호출
        return coupangApiClient.getCoupangProduct(apiDto);
    }

    public String addProduct(ProductDto productDto, String username) throws IOException { 
        // 데이터베이스에서 사용자 정보 가져오기
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 사용자 정보를 기반으로 CoupangApiDto 생성
        CoupangApiDto apiDto = new CoupangApiDto();
        apiDto.setApiKey(user.getCoupangApiKey());
        apiDto.setSecretKey(user.getCoupangSecretKey());
        apiDto.setSubId(user.getCoupangSubId());

        String partnersUrl = coupangApiClient.getPartnersLink(productDto.getProductUrl(), apiDto);
        // CoupangApiClient 호출
        return null;
    }
}