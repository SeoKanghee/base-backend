package com.kelly.base.common.i18n;

import com.kelly.base.identity.adapter.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@DisplayName("I18nInterceptorTests")
class I18nInterceptorTests {
    private final String defaultLanguageCode = "en";
    private final String defaultRegulator = "mfds";

    private SecurityContext mockSecurityContext;

    private HttpServletRequest mockRequest;
    private HttpServletResponse mockResponse;
    private Object mockObject;

    private I18nInterceptor i18nInterceptor;

    @BeforeEach
    void init() {
        mockRequest = mock(HttpServletRequest.class);
        mockResponse = mock(HttpServletResponse.class);
        mockObject = mock(Object.class);

        final I18nProperties dummyI18nProperties = new I18nProperties();
        dummyI18nProperties.setDefaultLanguage(defaultLanguageCode);
        dummyI18nProperties.setDefaultRegulator(defaultRegulator);

        i18nInterceptor = new I18nInterceptor(dummyI18nProperties);
    }

    @AfterEach
    void release() {
        // 각 테스트 후 I18nContext 정리
        I18nContext.clear();
    }

    @Nested
    @DisplayName("AuthenticationTests")
    class AuthenticationTests {
        private final String userLanguageCode = "ko";   // 인증 정보에 포함시킬 language code

        @BeforeEach
        void init() {
            mockSecurityContext = mock(SecurityContext.class);
            final Authentication authentication = mock(Authentication.class);
            final CustomUserDetails mockCustomUserDetails = mock(CustomUserDetails.class);

            when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(mockCustomUserDetails);
            when(mockCustomUserDetails.getLanguageCode()).thenReturn(userLanguageCode);
        }

        @Test
        @DisplayName("getLanguageCode test - 인증된 사용자인 경우")
        void getLanguageCodeWithAuthUserTest() {
            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder
                         = mockStatic(SecurityContextHolder.class)) {
                // given - mocking 된 SecurityContext 정의
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

                // when
                final String result = Assertions.assertDoesNotThrow(i18nInterceptor::getLanguageCode);

                // then
                Assertions.assertEquals(userLanguageCode, result);
            }
        }

        @Test
        @DisplayName("preHandle test - 인증된 사용자인 경우")
        void preHandleWithAuthUserTest() {
            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder
                         = mockStatic(SecurityContextHolder.class)) {
                // given - mocking 된 SecurityContext 정의
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

                // when
                final boolean result = Assertions.assertDoesNotThrow(
                        () -> i18nInterceptor.preHandle(mockRequest, mockResponse, mockObject)
                );

                // then
                Assertions.assertTrue(result);
                Assertions.assertEquals(I18nContext.getLanguage(), userLanguageCode);
                Assertions.assertEquals(I18nContext.getRegulator(), defaultRegulator);
            }
        }
    }

    @Nested
    @DisplayName("UnAuthenticationTests")
    class UnAuthenticationTests {
        @BeforeEach
        void init() {
            mockSecurityContext = mock(SecurityContext.class);
            when(mockSecurityContext.getAuthentication()).thenReturn(null); // 인증 정보를 null 로 반환
        }

        @Test
        @DisplayName("getLanguageCode test - 인증 정보가 없는 경우")
        void getLanguageCodeWithUnAuthTest() {
            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder
                         = mockStatic(SecurityContextHolder.class)) {
                // given
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

                // when
                final String languageCode = Assertions.assertDoesNotThrow(i18nInterceptor::getLanguageCode);

                // then - 인증 정보가 없어 기본값 반환
                Assertions.assertEquals(defaultLanguageCode, languageCode);
            }
        }

        @Test
        @DisplayName("preHandle test - 인증되지 않은 접근")
        void preHandleWithUnAuthTest() {
            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder
                         = mockStatic(SecurityContextHolder.class)) {
                // given
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

                // when
                final boolean result = Assertions.assertDoesNotThrow(
                        () -> i18nInterceptor.preHandle(mockRequest, mockResponse, mockObject)
                );

                // then
                Assertions.assertTrue(result);
                Assertions.assertEquals(defaultLanguageCode, I18nContext.getLanguage());
                Assertions.assertEquals(defaultRegulator, I18nContext.getRegulator());
            }
        }
    }

    @Nested
    @DisplayName("UnknownUserTests")
    class UnknownUserTests {
        @BeforeEach
        void init() {
            mockSecurityContext = mock(SecurityContext.class);
            final Authentication authentication = mock(Authentication.class);

            when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn("UnknownUser");
        }

        @Test
        @DisplayName("getLanguageCode test - Principal 이 CustomUserDetails가 아닌 경우")
        void getLanguageCodeNotCustomUserDetailsTest() {
            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder
                         = mockStatic(SecurityContextHolder.class)) {
                // given
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

                // when
                final String languageCode = Assertions.assertDoesNotThrow(i18nInterceptor::getLanguageCode);

                // then
                Assertions.assertEquals(defaultLanguageCode, languageCode);
            }
        }

        @Test
        @DisplayName("preHandle test - Principal 이 CustomUserDetails 가 아닌 경우")
        void preHandleWithUnAuthTest() {
            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder
                         = mockStatic(SecurityContextHolder.class)) {
                // given
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);

                // when
                final boolean result = Assertions.assertDoesNotThrow(
                        () -> i18nInterceptor.preHandle(mockRequest, mockResponse, mockObject)
                );

                // then
                Assertions.assertTrue(result);
                Assertions.assertEquals(defaultLanguageCode, I18nContext.getLanguage());
                Assertions.assertEquals(defaultRegulator, I18nContext.getRegulator());
            }
        }
    }

    @Nested
    @DisplayName("afterCompletionTests")
    class AfterCompletionTests {
        @Test
        @DisplayName("afterCompletion test - 정상적인 처리시에 I18nContext 정리 확인")
        void afterCompletionNormalTest() {
            // given
            I18nContext.setLanguage("ko");
            I18nContext.setRegulator("fda");

            // when
            i18nInterceptor.afterCompletion(mockRequest, mockResponse, mockObject, null);

            // then - languageCode 와 regulatorCode 가 null 로 변경됐는지 확인
            Assertions.assertNull(I18nContext.getLanguage());
            Assertions.assertNull(I18nContext.getRegulator());
        }

        @Test
        @DisplayName("afterCompletion - 예외가 있어도 I18nContext 가 정리되는지 확인")
        void shouldClearI18nContextEvenWithException() {
            // given
            I18nContext.setLanguage("en");
            I18nContext.setRegulator("mfds");
            Exception exception = new RuntimeException("test exception");

            // when
            i18nInterceptor.afterCompletion(mockRequest, mockResponse, mockObject, exception);

            // then - languageCode 와 regulatorCode 가 null 로 변경됐는지 확인
            Assertions.assertNull(I18nContext.getLanguage());
            Assertions.assertNull(I18nContext.getRegulator());
        }
    }
}
