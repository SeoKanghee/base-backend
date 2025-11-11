package com.kelly.base.common.crypto;

import com.kelly.base.common.config.CommonPropertiesConfig;
import com.kelly.base.common.interfaces.ICryptoService;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonCryptoService implements ICryptoService<byte[]> {
    private static final String KEY_ALGORITHM = "AES";
    private static final String ALGORITHM = "AES/GCM/NoPadding";    // AES/CBC/PKCS5Padding 에 취약성 문제가 있어 변경
    private static final int IV_LENGTH = 12;    // Initialization Vector 의 길이 ( 96 비트 )

    private byte[] cryptoKey = null;

    private final CommonPropertiesConfig commonPropertiesConfig;

    @PostConstruct
    void init() {
        setCryptoKey(commonPropertiesConfig.getCommonCryptoKey());
    }

    @Override
    public void setCryptoKey(final String key) {
        cryptoKey = key.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String encrypt(@NonNull final byte[] source) {
        if (isInvalidCryptoKey()) {
            log.error("cryptoKey is missing, making encryption impossible");
            return null;
        }

        try {
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom(); // 동기화 문제가 혹시 있을 수 있으니 매번 new 로 생성
            secureRandom.nextBytes(iv);

            SecretKeySpec secretKey = new SecretKeySpec(cryptoKey, KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

            byte[] encBytes = cipher.doFinal(source);
            byte[] combined = new byte[iv.length + encBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encBytes, 0, combined, iv.length, encBytes.length);

            return DatatypeConverter.printBase64Binary(combined);
        } catch (Exception e) {
            log.error("an error occurred during encryption - {} : {}", e.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    @Override
    public byte[] decrypt(@NonNull final String source) {
        if (isInvalidCryptoKey()) {
            log.error("cryptoKey is missing, making decryption impossible");
            return new byte[0];
        }

        try {
            byte[] combinedDecoded = DatatypeConverter.parseBase64Binary(source);
            byte[] iv = new byte[12];
            byte[] encBytes = new byte[combinedDecoded.length - iv.length];
            System.arraycopy(combinedDecoded, 0, iv, 0, iv.length);
            System.arraycopy(combinedDecoded, iv.length, encBytes, 0, encBytes.length);

            SecretKeySpec secretKey = new SecretKeySpec(cryptoKey, KEY_ALGORITHM);
            Cipher decipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);
            decipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

            return decipher.doFinal(encBytes);
        } catch (Exception e) {
            log.error("an error occurred during decryption - {} : {}", e.getClass().getSimpleName(), e.getMessage());
            return new byte[0];
        }
    }

    private boolean isInvalidCryptoKey() {
        return cryptoKey == null;
    }
}