package com.kelly.base.common.crypto;

import com.kelly.base.common.config.CommonPropertiesConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

@DisplayName("CommonCryptoServiceTests")
class CommonCryptoServiceTests {
    private CommonCryptoService commonCryptoService;

    @BeforeEach
    void initService() {
        final String cryptoKey = "valid-key-length-just-16";

        final CommonPropertiesConfig mockCommonPropertiesConfig = mock(CommonPropertiesConfig.class);
        when(mockCommonPropertiesConfig.getCommonCryptoKey()).thenReturn(cryptoKey);

        commonCryptoService = new CommonCryptoService(mockCommonPropertiesConfig);
    }

    @Nested
    @DisplayName("EncDecTests")
    class EncDecTests {
        @Test
        @DisplayName("encDec test - pass")
        void encDecPassTest() {
            // given
            commonCryptoService.init(); // property 에 정의된 key 읽음
            final String testSource = "TEST";

            // when, then - encrypt
            final String encStr = Assertions.assertDoesNotThrow(
                    () -> commonCryptoService.encrypt(testSource.getBytes(StandardCharsets.UTF_8))
            );
            Assertions.assertNotNull(encStr);
            System.out.println(testSource + " -> " + encStr);

            // when, then - decrypt
            final byte[] decBytes = Assertions.assertDoesNotThrow(
                    () -> commonCryptoService.decrypt(encStr)
            );
            Assertions.assertNotEquals(0, decBytes.length);

            // then - 암복호화 전후 결과 비교
            Assertions.assertEquals(testSource, new String(decBytes, StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("encDec test - fail : key is null")
        void encDecFailKeyNullTest() {
            // given - init 생략
            final String testSource = "TEST";

            // when, then - encrypt
            final String encStr = Assertions.assertDoesNotThrow(
                    () -> commonCryptoService.encrypt(testSource.getBytes(StandardCharsets.UTF_8))
            );
            Assertions.assertNull(encStr);

            // when, then - decrypt
            final byte[] decBytes = Assertions.assertDoesNotThrow(
                    () -> commonCryptoService.decrypt(testSource)   // encStr 이 null 이므로 그냥 testSource 전달
            );
            Assertions.assertEquals(0, decBytes.length);
        }

        @Test
        @DisplayName("encDec test - one-time crypto key")
        void encDecOneTimeCryptoKeyTest() {
            // given
            final String testSource = "TEST";
            final String oneTimeCryptoKey = "oneTimeCryptoKey";

            // when, then - encrypt
            final String encStr = Assertions.assertDoesNotThrow(
                    () -> commonCryptoService.encrypt(testSource.getBytes(StandardCharsets.UTF_8),
                                                      oneTimeCryptoKey)
            );
            Assertions.assertNotNull(encStr);
            System.out.println("key : " + oneTimeCryptoKey + ", " + testSource + " -> " + encStr);

            // when, then - decrypt
            final byte[] decBytes = Assertions.assertDoesNotThrow(
                    () -> commonCryptoService.decrypt(encStr, oneTimeCryptoKey)
            );
            Assertions.assertNotEquals(0, decBytes.length);

            // then - 암복호화 전후 결과 비교
            Assertions.assertEquals(testSource, new String(decBytes, StandardCharsets.UTF_8));
        }
    }

    @Nested
    @DisplayName("EncryptTests")
    class EncryptTests {
        @Test
        @DisplayName("encrypt test - fail : key length less than 16")
        void encFailWrongKeyTest() {
            // given
            commonCryptoService.setCryptoKey("123456789012345");
            final String testSource = "TEST";

            // when
            final String encStr = Assertions.assertDoesNotThrow(
                    () -> commonCryptoService.encrypt(testSource.getBytes(StandardCharsets.UTF_8))
            );

            // then - Invalid AES key length: 15 bytes
            Assertions.assertNull(encStr);
        }
    }

    @Nested
    @DisplayName("DecryptTests")
    class DecryptTests {
        @Test
        @DisplayName("decrypt test - fail : no encrypted source")
        void decFailWrongSourceTest() {
            // given
            commonCryptoService.init(); // property 에 정의된 key 읽음
            final String testSource = "test";

            // when
            final byte[] decBytes = Assertions.assertDoesNotThrow(
                    () -> commonCryptoService.decrypt(testSource)
            );

            // then - NegativeArraySizeException
            Assertions.assertEquals(0, decBytes.length);
        }
    }
}
