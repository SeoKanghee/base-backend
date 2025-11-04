package com.kelly.base.common.config;

import com.kelly.base.common.interfaces.ILibLoader;
import com.kelly.base.common.interfaces.IVault;
import com.kelly.base.common.jni.InternalLibLoader;
import com.kelly.base.common.jni.NativeVault;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Configuration
public class VaultConfig {
    // Key 값을 획득할 수 있는 Vault 의 선택지 구현

    @Bean
    @ConditionalOnProperty(
            name = "config.options.vault-type",
            havingValue = "native"
    )
    IVault nativeVault() {
        // 보안 key 를 활용하기 위한 Vault Bean 을 초기화
        log.info("use native vault");
        final ILibLoader libLoader = new InternalLibLoader();   // jar 내부 라이브러리 사용
        return new NativeVault(libLoader);  // 네이티브 라이브러리 사용
    }

    @Bean
    @ConditionalOnProperty(
            name = "config.options.vault-type",
            havingValue = "cloud"
    )
    IVault cloudVault() {
        // 현재는 지원되지 않음
        return () -> {
            log.error("cloud vault is not supported");
            return new byte[0];
        };
    }

    @Bean
    @ConditionalOnMissingBean(IVault.class) // property 가 정의되지 않았을 때 기본값 정의
    IVault randomVault() {
        // 임의 생성
        return () -> {
            log.info("use random vault");

            final String seedKey = UUID.randomUUID().toString();
            log.debug("generated seed key : {}", seedKey);

            return seedKey.getBytes(StandardCharsets.UTF_8);
        };
    }
}
