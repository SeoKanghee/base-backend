package com.kelly.base.product.shared.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)      // CSRF 비활성화 (REST API용)
            .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화 (REST API용)
            .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 비활성화

            // 세션 기반 인증 사용
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                    .maximumSessions(1) // 동시 세션 1개로 제한
                    .maxSessionsPreventsLogin(false)    // 새 로그인이 기존 세션 무효화
            )

            // URL별 인증 설정
            .authorizeHttpRequests(auth -> auth
                    // 인증 없이 접근 가능한 URL
                    .requestMatchers(
                            "/api/auth/login",
                            "/api/auth/logout",
                            "/api.html",
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/monitor/**"
                    ).permitAll()
                    // 그 외 모든 요청은 인증 필요
                    .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // AuthenticationManager bean 이 생성될 때 초기화에 사용될 PasswordEncoder bean 선언
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
