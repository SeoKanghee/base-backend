package com.kelly.base.common.config;

import com.kelly.base.common.interfaces.IVault;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CommonJasyptConfig {
    private final IVault vault;

    @Bean(name = "commonJasyptEncryptor")
    public StringEncryptor commonJasyptEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(getPassword());  // key for encrypt
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }

    String getPassword() {
        String password;
        try {
            byte[] bytesSeed = vault.getJasyptSeed();
            password = new String(bytesSeed);
        } catch (UnsatisfiedLinkError | NullPointerException e) {
            // JNI 로 라이브러리 로딩이 실패하면 임의로 아무 값이나 넣어서 초기화 시킴
            password = UUID.randomUUID().toString();
            log.error("[{}] library loading failed - {}", e.getClass().getSimpleName(), password);
        }
        return password;
    }
}
