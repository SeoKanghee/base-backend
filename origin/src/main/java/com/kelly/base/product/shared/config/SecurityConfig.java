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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();   // session 정보 관리
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher(); // httpSession 이벤트를 spring security 에 전달
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)      // CSRF 비활성화 (REST API용)
            .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 비활성화 (REST API용)
            .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 비활성화

            // 세션 정책 정의 -> NEVER
            // - Spring Security 에서 자동으로 처리하지 않음
            // - AuthService 에서 수동으로 생성해서 등록해서 사용
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.NEVER)
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
