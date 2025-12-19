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
        @DisplayName("getPassword test - exception")
        void getPasswordExceptionTest() {
            // given
            when(mockVault.getJasyptSeed()).thenThrow(NullPointerException.class);

            // when, then - 발생한 exception 을 그대로 상위로 전달
            Assertions.assertThrows(NullPointerException.class, commonJasyptConfig::getPassword);
        }

        @Test
        @DisplayName("getPassword test - UnsatisfiedLinkError")
        void getPasswordUnsatisfiedLinkErrorTest() {
            // given
            when(mockVault.getJasyptSeed()).thenThrow(UnsatisfiedLinkError.class);

            // when, then - 발생한 exception 을 그대로 상위로 전달
            Assertions.assertThrows(UnsatisfiedLinkError.class, commonJasyptConfig::getPassword);
        }
    }

    @Nested
    @DisplayName("EncDecTests")
    class EncDecTests {
        @Test
        @DisplayName("encryption test")
        void encryptionTest() {
            // given
            final String jasyptSeed = "oXe/6YLvAKrjLadJIvUsv90JbFO4cxSZRFMAAR8mjoY";
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
