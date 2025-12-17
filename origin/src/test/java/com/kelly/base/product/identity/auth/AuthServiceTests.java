package com.kelly.base.product.identity.auth;

import com.kelly.base.common.exception.CommonRuntimeException;
import com.kelly.base.common.response.CommonResponse;
import com.kelly.base.common.response.CommonResultCode;
import com.kelly.base.common.utils.DateTimeUtil;
import com.kelly.base.product.identity.adapter.security.CustomUserDetailsService;
import com.kelly.base.product.identity.auth.dto.PostLoginRequest;
import com.kelly.base.product.identity.auth.strategy.session.AuthSessionManager;
import com.kelly.base.product.identity.auth.strategy.session.AuthSessionStrategy;
import com.kelly.base.product.identity.domain.account.Account;
import com.kelly.base.product.identity.domain.account.AccountStatus;
import com.kelly.base.product.identity.repository.AccountRepository;
import com.kelly.base.common.config.QuerydslConfig;
import com.kelly.base.product.identity.response.IdentityResultCode;
import com.kelly.base.product.identity.config.SecurityConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Objects;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import({
        AuthService.class, AuthSessionManager.class, AuthSessionStrategy.class, // session 기반으로 테스트
        CustomUserDetailsService.class,
        SecurityConfig.class, QuerydslConfig.class
})
@DisplayName("AuthServiceTests")
class AuthServiceTests {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuthService authService;

    @Nested
    @DisplayName("LoginTests")
    class LoginTests {
        @Nested
        @DisplayName("LoginTests - 일반 검증")
        class LoginNormalTests {
            @ParameterizedTest
            @CsvSource({
                    "adv_user, adv_user@password, 82000001",    // 처음 로그인 -> IdentityResultCode.NEED_CHANGE_PASSWORD
                    "gen-user, gen-user!password, 200",         // 일반 로그인 -> CommonResultCode.SUCCESS
            })
            @DisplayName("login test - 성공")
            void loginPassTest(final String loginId, final String password, final int expectedCode) {
                // given
                final PostLoginRequest postLoginRequest = new PostLoginRequest(loginId, password, true);
                final MockHttpServletRequest request = new MockHttpServletRequest();

                // when, then
                final CommonResponse<Void> result = Assertions.assertDoesNotThrow(
                        () -> authService.login(postLoginRequest, request));

                Assertions.assertEquals(expectedCode, result.getCode());
            }

            @Test
            @DisplayName("login test - 비밀번호 틀림")
            void loginFail1TimeTest() {
                // 비밀번호가 틀렸을 때 계정 로그인 검증
                // given - 저장된 계정 정보 확보
                final String loginId = "gen-user";
                final Account account = accountRepository.findByLoginId(loginId).orElseThrow();
                final long beforeFailCount = account.getFailCount();

                // given - 툴린 비밀번호로 로그인 시도
                final PostLoginRequest wrongRequest = new PostLoginRequest(loginId, "wrong_password", true);
                final MockHttpServletRequest request = new MockHttpServletRequest();

                // when - 비밀번호가 틀렸으므로 BadCredentialsException 이 발생
                final CommonRuntimeException exception1 = Assertions.assertThrows(
                        CommonRuntimeException.class,
                        () -> authService.login(wrongRequest, request)
                );

                // then - BAD_CREDENTIAL 이 발생했는지 확인
                Assertions.assertEquals(IdentityResultCode.BAD_CREDENTIAL, exception1.getResultCode());

                // then - fail count 가 1 증가했는지 확인
                Assertions.assertEquals(beforeFailCount + 1, account.getFailCount());
            }

            @Test
            @DisplayName("login test - 없는 아이디로 로그인 시도")
            void loginFailNotExistTest() {
                // 없는 아이디로 로그인 시도

                // given
                final String loginId = "not_exist";
                final PostLoginRequest postLoginRequest = new PostLoginRequest(loginId, loginId, true);
                final MockHttpServletRequest request = new MockHttpServletRequest();

                // when, then
                final CommonRuntimeException exception = Assertions.assertThrows(
                        CommonRuntimeException.class,
                        () -> authService.login(postLoginRequest, request)

                );
                // then - BAD_CREDENTIAL 이 발생했는지 확인
                Assertions.assertEquals(IdentityResultCode.BAD_CREDENTIAL, exception.getResultCode());
            }

            @Test
            @DisplayName("login test - Role 이 잘못된 경우")
            void loginFailRoleErrorTest() {
                // 로그인 시도 중 Role 이 잘못되어 처리 불가능한 경우

                // given
                final String loginId = "unknown_role";
                final PostLoginRequest postLoginRequest = new PostLoginRequest(loginId, loginId, true);
                final MockHttpServletRequest request = new MockHttpServletRequest();

                // when, then
                final CommonRuntimeException exception = Assertions.assertThrows(
                        CommonRuntimeException.class,
                        () -> authService.login(postLoginRequest, request)

                );
                // then - DATABASE_ERROR 이 발생했는지 확인
                Assertions.assertEquals(CommonResultCode.DATABASE_ERROR, exception.getResultCode());
            }
        }

        @Nested
        @DisplayName("LoginMockTests - Mock 을 이용한 검증")
        class LoginMockTests {
            @MockitoBean
            private CustomUserDetailsService mockUserDetailsService;

            @Autowired
            private AuthService authServiceWithMock;

            @Test
            @DisplayName("login test - 알 수 없는 exception 발생")
            void loginUnknownExceptionTest() {
                // 알 수 없는 exception 발생으로 처리가 불가능한 경우

                // given - loadUserByUsername 을 처리할 때 임의의 RuntimeException 을 발생
                when(mockUserDetailsService.loadUserByUsername(anyString())).thenThrow(RuntimeException.class);
                final String loginId = "unknown_role";
                final PostLoginRequest postLoginRequest = new PostLoginRequest(loginId, loginId, true);
                final MockHttpServletRequest request = new MockHttpServletRequest();

                // when
                final InternalAuthenticationServiceException result = Assertions.assertThrows(
                        InternalAuthenticationServiceException.class,
                        () -> authServiceWithMock.login(postLoginRequest, request)
                );

                // then - mocking 으로 던진 exception 이 InternalAuthenticationServiceException 으로 래핑됐는지 확인
                Assertions.assertInstanceOf(RuntimeException.class, result.getCause());
            }
        }

        @Nested
        @DisplayName("LoginTests - 상태 변경에 따른 검증")
        class LoginStatusTests {
            @Test
            @DisplayName("login test - 잠금 해제되어 로그인 성공")
            void loginPassWithUnlockTest() {
                // 일반 사용자를 잠금 상태로 변경 -> expired 시간을 10분전으로 설정

                // given - lock 상태 설정
                final String loginId = "gen-user";
                final String password = "gen-user!password";
                final Account account = accountRepository.findByLoginId(loginId).orElseThrow();
                account.lockAccount(DateTimeUtil.nowUtcMinusMinutes(10L));

                // given
                final PostLoginRequest postLoginRequest = new PostLoginRequest(loginId, password, true);
                final MockHttpServletRequest request = new MockHttpServletRequest();

                // when, then
                final CommonResponse<Void> result = Assertions.assertDoesNotThrow(
                        () -> authService.login(postLoginRequest, request));

                Assertions.assertEquals(CommonResultCode.SUCCESS.getCode(), result.getCode());
            }

            @Test
            @DisplayName("login test - 비밀번호 6번 틀림")
            void loginFail6TimesTest() {
                // 비밀번호가 5번 틀린 계정으로 로그인 검증
                final MockHttpServletRequest request = new MockHttpServletRequest();

                // ------------------------------------------------------------------------------------------------------ //
                // [1] 비밀번호가 1번 더 틀려서 계정이 잠김
                // ------------------------------------------------------------------------------------------------------ //
                // given - 툴린 비밀번호로 로그인 시도
                final String loginId = "failure5";
                final PostLoginRequest wrongRequest = new PostLoginRequest(loginId, "wrong_password", true);

                // when - 비밀번호가 틀렸으므로 BadCredentialsException 이 발생
                final CommonRuntimeException exception1 = Assertions.assertThrows(
                        CommonRuntimeException.class,
                        () -> authService.login(wrongRequest, request)
                );

                // then - BAD_CREDENTIAL 이 발생했는지 확인
                Assertions.assertEquals(IdentityResultCode.BAD_CREDENTIAL, exception1.getResultCode());

                // then - 계정이 잠겼는지 확인
                final Account account = accountRepository.findByLoginId(loginId).orElseThrow();
                Assertions.assertEquals(AccountStatus.LOCKED, account.getStatus());

                // ------------------------------------------------------------------------------------------------------ //
                // [2] 잠긴 계정으로 로그인 시도
                // ------------------------------------------------------------------------------------------------------ //
                // given - 맞는 비밀번호로 로그인 시도
                final PostLoginRequest correctRequest = new PostLoginRequest(loginId, loginId, true);

                // when - 계정이 잠겼으므로 LockedException 이 발생
                final CommonRuntimeException exception2 = Assertions.assertThrows(
                        CommonRuntimeException.class,
                        () -> authService.login(correctRequest, request)
                );

                // then - ACCOUNT_LOCKED 가 발생했는지 확인
                Assertions.assertEquals(IdentityResultCode.ACCOUNT_LOCKED, exception2.getResultCode());
            }

            @ParameterizedTest
            @CsvSource({
                    "INACTIVE",
                    "DELETED",
            })
            @DisplayName("login test - 비활성화된 계정")
            void loginDisableTest(final AccountStatus accountStatus) {
                // 비활성화 상태일 때 계정 로그인 검증
                final String loginId = "disable";
                final Account account = accountRepository.findByLoginId(loginId).orElseThrow();
                account.updateStatus(accountStatus);    // INACTIVE or DELETED

                // given - INACTIVE 상태인 계정 로그인 시도
                final PostLoginRequest postLoginRequest = new PostLoginRequest(loginId, loginId, true);
                final MockHttpServletRequest request = new MockHttpServletRequest();

                // when - INACTIVE 상태이므로 DisabledException 이 발생
                final CommonRuntimeException exception = Assertions.assertThrows(
                        CommonRuntimeException.class,
                        () -> authService.login(postLoginRequest, request)
                );

                // then - ACCOUNT_DISABLED 이 발생했는지 확인
                Assertions.assertEquals(IdentityResultCode.ACCOUNT_DISABLED, exception.getResultCode());
            }

            @Test
            @DisplayName("login test - 잠금 해제되어 로그인 성공 (expire date 가 null)")
            void loginPassWithExpireNullTest() {
                // 잠긴 계정인데 expired 시간이 null 인 경우 ( 일반적인 상황에서 발생하지 않는 예외 케이스 )

                // given - lock 상태인데 lockout_expired_at 가 null 인 경우
                final String loginId = "invalid";
                final PostLoginRequest postLoginRequest = new PostLoginRequest(loginId, loginId, true);
                final MockHttpServletRequest request = new MockHttpServletRequest();

                // when, then
                final CommonResponse<Void> result = Assertions.assertDoesNotThrow(
                        () -> authService.login(postLoginRequest, request));

                Assertions.assertEquals(CommonResultCode.SUCCESS.getCode(), result.getCode());
            }
        }

        @Nested
        @DisplayName("LoginTests - 세션 검증")
        class LoginSessionTests {
            @Test
            @DisplayName("login test - 이미 로그인 되어 있는 경우 같은 세션으로 접근")
            void loginPassAlreadyLoginSameSessionTest() {
                // given
                final String loginId = "gen-user";
                final String password = "gen-user!password";
                final PostLoginRequest postLoginRequest = new PostLoginRequest(loginId, password, true);
                final MockHttpServletRequest request = new MockHttpServletRequest();

                // given - 로그인
                final CommonResponse<Void> preResult = Assertions.assertDoesNotThrow(
                        () -> authService.login(postLoginRequest, request));
                Assertions.assertEquals(CommonResultCode.SUCCESS.getCode(), preResult.getCode());

                // when - 두번째 로그인 시도 ( 기존 세션으로 동일하게 로그인 시도 )
                final PostLoginRequest newPostLoginRequest = new PostLoginRequest(loginId, password, false);
                final CommonResponse<Void> result = Assertions.assertDoesNotThrow(
                        () -> authService.login(newPostLoginRequest, request));

                // then
                Assertions.assertEquals(CommonResultCode.SUCCESS.getCode(), result.getCode());
            }

            @Test
            @DisplayName("login test - 이미 로그인 되어 있는 경우 다른 세션으로 접근")
            void loginFailAlreadyLoginNewSessionTest() {
                // given
                final String loginId = "gen-user";
                final String password = "gen-user!password";
                final PostLoginRequest postLoginRequest = new PostLoginRequest(loginId, password, true);
                final MockHttpServletRequest request = new MockHttpServletRequest();

                // given - 로그인
                final CommonResponse<Void> preResult = Assertions.assertDoesNotThrow(
                        () -> authService.login(postLoginRequest, request));
                Assertions.assertEquals(CommonResultCode.SUCCESS.getCode(), preResult.getCode());

                // when - 두번째 로그인 시도
                final MockHttpServletRequest newRequest = new MockHttpServletRequest(); // 새로운 세션으로 접근
                final PostLoginRequest newPostLoginRequest = new PostLoginRequest(loginId, password, false);

                final CommonRuntimeException exception = Assertions.assertThrows(
                        CommonRuntimeException.class,
                        () -> authService.login(newPostLoginRequest, newRequest));

                // then - ALREADY_LOGIN 이 발생했는지 확인
                Assertions.assertEquals(IdentityResultCode.ALREADY_LOGIN, exception.getResultCode());
            }

            @Test
            @DisplayName("login test - 이미 로그인 되어 있는 경우 다른 세션에서 강제로 로그인")
            void loginPassAlreadyLoginNewSessionForceTest() {
                // given
                final String loginId = "gen-user";
                final String password = "gen-user!password";
                final PostLoginRequest postLoginRequest = new PostLoginRequest(loginId, password, true);
                final MockHttpServletRequest request = new MockHttpServletRequest();

                // given - 로그인
                final CommonResponse<Void> preResult = Assertions.assertDoesNotThrow(
                        () -> authService.login(postLoginRequest, request));
                Assertions.assertEquals(CommonResultCode.SUCCESS.getCode(), preResult.getCode());

                // when - 두번째 로그인 시도 (다른 세션)
                final MockHttpServletRequest newRequest = new MockHttpServletRequest();
                newRequest.setSession(Objects.requireNonNull(newRequest.getSession(true)));
                final PostLoginRequest newPostLoginRequest = new PostLoginRequest(loginId, password, true);

                final CommonResponse<Void> result = Assertions.assertDoesNotThrow(
                        () -> authService.login(newPostLoginRequest, newRequest));

                // then
                Assertions.assertEquals(CommonResultCode.SUCCESS.getCode(), result.getCode());
            }
        }
    }


    @Nested
    @DisplayName("LogoutTests")
    class LogoutTests {
        @Test
        @DisplayName("logout test - 일반 검증")
        void logoutPassTest() {
            // given
            final String loginId = "gen-user";
            final String password = "gen-user!password";
            final PostLoginRequest postLoginRequest = new PostLoginRequest(loginId, password, true);
            final MockHttpServletRequest request = new MockHttpServletRequest();

            // given - 로그인
            final CommonResponse<Void> loginResult = Assertions.assertDoesNotThrow(
                    () -> authService.login(postLoginRequest, request));
            Assertions.assertEquals(CommonResultCode.SUCCESS.getCode(), loginResult.getCode());

            // when - 로그아웃
            final CommonResponse<Void> logoutResult = Assertions.assertDoesNotThrow(
                    () -> authService.logout(request)
            );
            Assertions.assertEquals(CommonResultCode.SUCCESS.getCode(), logoutResult.getCode());
        }

        @Test
        @DisplayName("logout test - 세션이 null 인 경우")
        void logoutSessionNullTest() {
            // 일반적으로는 spring security 입구컷이라 발생하지 않는 케이스

            // given
            final MockHttpServletRequest request = new MockHttpServletRequest();

            // when
            Assertions.assertDoesNotThrow(
                    () -> authService.logout(request)
            );
        }
    }
}
