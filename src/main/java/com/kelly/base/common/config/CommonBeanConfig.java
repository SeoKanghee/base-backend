package com.kelly.base.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelly.base.common.utils.JsonUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class CommonBeanConfig {
    @Bean
    @Primary    // 혹시나 모를 다른 ObjectMapper bean 과 충돌 방지
    ObjectMapper objectMapper() {
        // ObjectMapper 를 app 내에서 단일 인스턴스로 유지하기 위해 static 으로 생성된 JsonUtil 내부 filed 를 재사용
        return JsonUtil.objectMapper;
    }
}
