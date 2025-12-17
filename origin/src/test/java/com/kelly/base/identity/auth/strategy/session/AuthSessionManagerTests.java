package com.kelly.base.identity.auth.strategy.session;

import com.kelly.base.identity.auth.strategy.session.AuthSessionManager;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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

    @Nested
    @DisplayName("ExtractLoginIdTests")
    class ExtractLoginIdTests {
        @Test
        @DisplayName("extractLoginId test - exception 이 발생하는 경우")
        void extractLoginIdExceptionTest() {
            // exception 이 발생했을 경우 'unknown' 이 반환되는지 확인

            // when - NullPointerException
            final String result = Assertions.assertDoesNotThrow(
                    () -> ReflectionTestUtils.invokeMethod(
                            authSessionManager, "extractLoginId", mock(HttpSession.class)
                    )
            );

            // then
            Assertions.assertEquals("unknown", result);
        }
    }

    @Nested
    @DisplayName("ExpireSessionInfoTests")
    class ExpireSessionInfoTests {
        @Test
        @DisplayName("expireSessionInfo test - 등록된 session 을 찾지 못했을 경우")
        void expireSessionInfoNullTest() {
            // sessionId 가 없어도 exception 이 발생하지 않는지 확인

            // when, then
            Assertions.assertDoesNotThrow(
                    () -> ReflectionTestUtils.invokeMethod(
                            authSessionManager, "expireSessionInfo", "session-id", "loginId"
                    )
            );
        }
    }
}
