package com.kelly.base.common.crypto;

import com.kelly.base.common.config.CommonPropertiesConfig;
import com.kelly.base.common.interfaces.ICryptoService;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
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
        // ASE key 길이 제한
        // - AES-128 : 16 / AES-192 : 24 / AES-256 : 32
        setCryptoKey(commonPropertiesConfig.getCommonCryptoKey());
    }

    /**
     * 암호화 키 설정
     *
     * @param key 저장할 암호화 키
     * @author kelly
     */
    @Override
    public void setCryptoKey(final String key) {
        cryptoKey = key.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 내장된 키로 암호화
     *
     * @param source 암호화할 byte 배열
     * @return 암호화된 문자열
     * @author kelly
     */
    @Override
    public String encrypt(final byte[] source) {
        if (isInvalidCryptoKey()) {
            log.error("cryptoKey is missing, making encryption impossible");
            return null;
        }
        return dispatchEncryption(source, cryptoKey);
    }

    /**
     * 일회용 암호화 키로 암호화
     *
     * @param source 암호화할 byte 배열
     * @param oneTimeCryptoKey 일회용 암호화 키
     * @return 암호화된 문자열
     * @author kelly
     */
    public String encrypt(final byte[] source, @NonNull final String oneTimeCryptoKey) {
        final byte[] customCryptoKey = oneTimeCryptoKey.getBytes(StandardCharsets.UTF_8);
        return dispatchEncryption(source, customCryptoKey);
    }

    String dispatchEncryption(final byte[] source, final byte[] customCryptoKey) {
        try {
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom secureRandom = new SecureRandom(); // 동기화 문제가 혹시 있을 수 있으니 매번 new 로 생성
            secureRandom.nextBytes(iv);

            SecretKeySpec secretKey = new SecretKeySpec(customCryptoKey, KEY_ALGORITHM);
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

    /**
     * 내장된 키로 복호화
     *
     * @param source 암호화된 문자열
     * @return 복호화된 byte 배열
     * @author kelly
     */
    @Override
    public byte[] decrypt(@NonNull final String source) {
        if (isInvalidCryptoKey()) {
            log.error("cryptoKey is missing, making decryption impossible");
            return new byte[0];
        }
        return dispatchDecryption(source, cryptoKey);
    }

    /**
     * 일회용 암호화 키로 복호화
     *
     * @param source 암호화된 문자열
     * @param oneTimeCryptoKey 일회용 암호화 키
     * @return 복호화된 byte 배열
     * @author kelly
     */
    public byte[] decrypt(@NonNull final String source, @NonNull final String oneTimeCryptoKey) {
        final byte[] customCryptoKey = oneTimeCryptoKey.getBytes(StandardCharsets.UTF_8);
        return dispatchDecryption(source, customCryptoKey);
    }

    byte[] dispatchDecryption(@NonNull final String source, @NonNull final byte[] customCryptoKey) {
        try {
            byte[] combinedDecoded = DatatypeConverter.parseBase64Binary(source);
            byte[] iv = new byte[IV_LENGTH];
            byte[] encBytes = new byte[combinedDecoded.length - iv.length];
            System.arraycopy(combinedDecoded, 0, iv, 0, iv.length);
            System.arraycopy(combinedDecoded, iv.length, encBytes, 0, encBytes.length);

            SecretKeySpec secretKey = new SecretKeySpec(customCryptoKey, KEY_ALGORITHM);
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