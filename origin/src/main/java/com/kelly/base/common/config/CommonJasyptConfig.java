package com.kelly.base.common.config;

import com.kelly.base.common.interfaces.IVault;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        config.setKeyObtentionIterations("210000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }

    String getPassword() {
        try {
            byte[] bytesSeed = vault.getJasyptSeed();
            return new String(bytesSeed);
        } catch (Exception | UnsatisfiedLinkError e) {
            // Native Library 를 사용할 경우 LinkError 발생 가능성 존재
            log.error("[{}] library loading failed", e.getClass().getSimpleName());
            throw e;
        }
    }
}
