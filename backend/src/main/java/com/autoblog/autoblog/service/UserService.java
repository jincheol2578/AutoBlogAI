package com.autoblog.autoblog.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.autoblog.autoblog.domain.Role;
import com.autoblog.autoblog.domain.User;
import com.autoblog.autoblog.dto.ApiKeyDto;
import com.autoblog.autoblog.dto.RegisterDto;
import com.autoblog.autoblog.repository.UserRepository;
import com.autoblog.autoblog.util.CommonUtills;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegisterDto registerDto) {
        // 중복 사용자 검증
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new RuntimeException("이미 존재하는 사용자입니다.");
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 새 사용자 생성
        User user = User.builder()
                .username(registerDto.getUsername())
                .email(registerDto.getEmail())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .role(Role.USER) // 기본 권한 부여
                .build();

        userRepository.save(user);
    }

    public void updateUserInfo(String username, ApiKeyDto dto) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        CommonUtills.updateFieldIfNotEmpty(dto.getSubId(), user::setCoupangSubId);
        CommonUtills.updateFieldIfNotEmpty(dto.getApiKey(), user::setCoupangApiKey);
        CommonUtills.updateFieldIfNotEmpty(dto.getSecretKey(), user::setCoupangSecretKey);
        CommonUtills.updateFieldIfNotEmpty(dto.getVivoldiKey(), user::setVivoldiApiKey); 
        CommonUtills.updateFieldIfNotEmpty(dto.getVivoldiId(), user::setVivoldiId);

        userRepository.save(user);
    }
}
