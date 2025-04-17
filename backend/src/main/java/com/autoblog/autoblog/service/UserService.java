package com.autoblog.autoblog.service;

import com.autoblog.autoblog.domain.Role;
import com.autoblog.autoblog.domain.User;
import com.autoblog.autoblog.dto.CoupangApiDto;
import com.autoblog.autoblog.dto.RegisterDto;
import com.autoblog.autoblog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public void updateCoupangInfo(String username, CoupangApiDto dto) {
        if (dto.getSubId() == null || dto.getSubId().isEmpty()) {
            throw new IllegalArgumentException("Invalid data");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        user.setCoupangSubId(dto.getSubId());
        user.setCoupangApiKey(dto.getApiKey());
        user.setCoupangSecretKey(dto.getSecretKey());

        userRepository.save(user);
    }
}
