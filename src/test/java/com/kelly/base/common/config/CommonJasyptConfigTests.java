package com.kelly.base.common.config;

import com.kelly.base.common.interfaces.IVault;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

@DisplayName("CommonJasyptConfigTests")
class CommonJasyptConfigTests {
    private CommonJasyptConfig commonJasyptConfig;

    private IVault mockVault;

    @BeforeEach
    void init() {
        mockVault = mock(IVault.class);
        commonJasyptConfig = new CommonJasyptConfig(mockVault);
    }

    @Nested
    @DisplayName("GetPasswordTests")
    class GetPasswordTests {
        @Test
        @DisplayName("getPassword test - pass")
        void getPasswordPassTest() {
            // given
            final String seed = "abc";
            when(mockVault.getJasyptSeed()).thenReturn(seed.getBytes(StandardCharsets.UTF_8));

            // when
            final String result = Assertions.assertDoesNotThrow(commonJasyptConfig::getPassword);

            // then
            Assertions.assertEquals(seed, result);
        }

        @Test
        @DisplayName("getPassword test - fail")
        void getPasswordFailTest() {
            // given
            when(mockVault.getJasyptSeed()).thenThrow(UnsatisfiedLinkError.class);

            // when - getJasyptSeed 에서는 exception 이 발생해도, getPassword 는 임의의 값을 반환해야 함
            final String result = Assertions.assertDoesNotThrow(commonJasyptConfig::getPassword);

            // then
            Assertions.assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("EncDecTests")
    class EncDecTests {
        private final String jasyptSeed = "oXe/6YLvAKrjLadJIvUsv90JbFO4cxSZRFMAAR8mjoY";

        @Test
        @DisplayName("encryption test")
        void encryptionTest() {
            // given
            when(mockVault.getJasyptSeed()).thenReturn(jasyptSeed.getBytes(StandardCharsets.UTF_8));
            final StringEncryptor encryptor = commonJasyptConfig.commonJasyptEncryptor();
            final String source = "root";

            // when
            final String result = Assertions.assertDoesNotThrow(
                    () -> encryptor.encrypt(source)
            );

            // then
            Assertions.assertNotNull(result);
            System.out.println(source + " -> " + result);
        }
    }
}
