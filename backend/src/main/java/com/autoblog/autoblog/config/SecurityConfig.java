package com.autoblog.autoblog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.autoblog.autoblog.security.JwtAuthenticationFilter;
import com.autoblog.autoblog.security.JwtTokenProvider;
import com.autoblog.autoblog.service.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 비활성화
            .httpBasic(httpBasic -> httpBasic.disable()) // 기본 로그인 폼 비활성화
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안 함
            .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll() // 로그인 회원가입 접근 허용
            .requestMatchers("/h2-console/**").permitAll() // H2 콘솔 접근 허용
            .anyRequest().authenticated() // 나머지 경로는 인증 필요
        )
            .headers(headers -> headers.frameOptions().sameOrigin()) // H2 콘솔 사용을 위한 설정 개발 환경에서만 사용
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, customUserDetailsService),
                             UsernamePasswordAuthenticationFilter.class); // JWT 필터 등록

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
