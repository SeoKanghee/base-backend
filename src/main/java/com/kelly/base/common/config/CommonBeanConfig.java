package com.kelly.base.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonBeanConfig {
    @Bean
    ObjectMapper objectMapper() {
        // ObjectMapper 는 여러 곳에서 자주 사용되므로 bean 으로 선언
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Java 8 date/time type
        return objectMapper;
    }
}
