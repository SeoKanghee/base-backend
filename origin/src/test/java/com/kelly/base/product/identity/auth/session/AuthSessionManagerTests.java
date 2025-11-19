package com.kelly.base.product.identity.auth.session;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;

@DisplayName("AuthSessionManagerTests")
class AuthSessionManagerTests {
    private AuthSessionManager authSessionManager;

    @BeforeEach
    void init() {
        authSessionManager = new AuthSessionManager(mock(SessionRegistry.class));
    }

    @Nested
    @DisplayName("IsNewSessionTests")
    class IsNewSessionTests {
        @ParameterizedTest
        @CsvSource({
                ", , true", // prev 값이 null 이므로 now 에 상관 없이 new session
                ", just-now, true", // prev 값이 null 이므로 now 에 상관 없이 new session
                "prev, just-now, true", // prev 값이 있지만, now 와 다르므로 new session
                "same, same, false",    // prev 와 now 가 동일하므로 기존 session ( refresh 처리 )
        })
        @DisplayName("isNewSession test")
        void isNewSessionTest(final String prevSessionId, final String justNowSessionId, final boolean expectedResult) {
            // session ID 타이밍 이슈 재현이 어려우므로 컨디션만 체크

            // when
            final boolean result = Assertions.assertDoesNotThrow(
                    () -> ReflectionTestUtils.invokeMethod(
                            authSessionManager, "isNewSession", prevSessionId, justNowSessionId
                    )
            );

            // then
            Assertions.assertEquals(expectedResult, result);
        }
    }
}
