package com.kelly.base.common.jni;

import com.kelly.base.common.interfaces.IInternalLibLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("NativeVaultTests")
class NativeVaultTests {
    private NativeVault nativeVault;

    @Nested
    @Order(1)
    @DisplayName("LibraryLoadingSuccessfulTests")
    class LibraryLoadingSuccessfulTests {
        @BeforeEach
        void init() {
            final IInternalLibLoader internalLibLoader = new InternalLibLoader();
            nativeVault = new NativeVault(internalLibLoader);
            nativeVault.init();
        }

        @Test
        @DisplayName("getJasyptSeed test")
        void getJasyptSeedTest() {
            // given
            final String expectedSeed = "oXe/6YLvAKrjLadJIvUsv90JbFO4cxSZRFMAAR8mjoY";

            // when
            byte[] resultBytes = Assertions.assertDoesNotThrow(nativeVault::getJasyptSeed);

            // then
            final String result = new String(resultBytes);
            Assertions.assertEquals(expectedSeed, result);
        }
    }

    @Nested
    @Order(999)
    @DisplayName("LibraryLoadingFailedTests")
    class LibraryLoadingFailedTests {
        private IInternalLibLoader mockInternalLibLoader;

        @BeforeEach
        void init() {
            mockInternalLibLoader = mock(IInternalLibLoader.class);
            nativeVault = new NativeVault(mockInternalLibLoader);
        }

        @Test
        @DisplayName("loadLibrary test - exception")
        void loadLibraryExceptionTest() {
            // given - resource stream 읽을 때 NullPointerException 발생
            when(mockInternalLibLoader.getLibInputStream(anyString(), anyString()))
                    .thenThrow(NullPointerException.class);

            // when, then - exception 은 내부적으로 처리되므로 bean 초기화에는 영향 없음
            Assertions.assertDoesNotThrow(nativeVault::init);
        }
    }
}
