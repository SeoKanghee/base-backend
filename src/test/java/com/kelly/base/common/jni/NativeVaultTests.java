package com.kelly.base.common.jni;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.mockito.MockedStatic;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("NativeVaultTests")
class NativeVaultTests {
    @Nested
    @Order(1)
    @DisplayName("LibraryLoadingSuccessfulTests")
    class LibraryLoadingSuccessfulTests {
        @Test
        @DisplayName("getJasyptSeed test")
        void getJasyptSeedTest() {
            // given
            final String expectedSeed = "oXe/6YLvAKrjLadJIvUsv90JbFO4cxSZRFMAAR8mjoY";
            NativeVault nativeVault = new NativeVault();

            // when
            byte[] resultBytes = Assertions.assertDoesNotThrow(
                    nativeVault::getJasyptSeed
            );
            final String result = new String(resultBytes);

            // then
            Assertions.assertEquals(expectedSeed, result);
        }
    }

    @Nested
    @Order(999)
    @DisplayName("LibraryLoadingFailedTests")
    class LibraryLoadingFailedTests {
        @Test
        @DisplayName("loadLibrary test")
        void loadLibraryTest() {
            try (MockedStatic<InternalLibLoader> mockedInternalLibLoader = mockStatic(InternalLibLoader.class)) {
                Assertions.assertNotNull(mockedInternalLibLoader);

                // given
                when(InternalLibLoader.getLibInputStream(anyString())).thenThrow(NullPointerException.class);

                // when - 인스턴스 생성시 loadLibrary 호출
                final NativeVault result = Assertions.assertDoesNotThrow(
                        NativeVault::new
                );

                // then
                Assertions.assertNotNull(result);
            }
        }
    }
}
