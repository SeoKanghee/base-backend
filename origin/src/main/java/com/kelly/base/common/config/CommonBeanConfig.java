package com.kelly.base.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelly.base.common.interfaces.IAuditContextProvider;
import com.kelly.base.common.audit.provider.DefaultAuditContextProvider;
import com.kelly.base.common.utils.JsonUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 공통으로 사용할 bean 선언
 *
 * @author 서강희
 */
@Configuration
public class CommonBeanConfig {
    /**
     * 기본 설정을 포함한 ObjectMapper 를 공유하기 위한 bean 선언
     * <p>
     * 상세 설정은 <code>JsonUtil</code> 에 정의되어 있습니다.<br>
     * 혹시나 모를 다른 ObjectMapper bean 과 충돌 방지를 위해 @Primary 적용됐습니다.
     *
     * @return ObjectMapper 인스턴스
     */
    @Bean
    @Primary
    ObjectMapper objectMapper() {
        // ObjectMapper 를 app 내에서 단일 인스턴스로 유지하기 위해 static 으로 생성된 JsonUtil 내부 filed 를 재사용
        return JsonUtil.objectMapper;
    }

    /**
     * AuditContextProvider 의 구현제가 없을 경우 DefaultAuditContextProvider 를 사용하기 위한 bean 선언
     *
     * @return DefaultAuditContextProvider 인스턴스
     */
    @Bean
    @ConditionalOnMissingBean(IAuditContextProvider.class)
    IAuditContextProvider defaultAuditContextProvider() {
        return new DefaultAuditContextProvider();
    }
}
