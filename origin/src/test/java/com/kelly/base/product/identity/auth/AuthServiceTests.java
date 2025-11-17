package com.kelly.base.product.identity.auth;

import com.kelly.base.common.exception.CommonRuntimeException;
import com.kelly.base.common.response.CommonResponse;
import com.kelly.base.common.response.CommonResultCode;
import com.kelly.base.common.utils.DateTimeUtil;
import com.kelly.base.product.identity.adapter.security.CustomUserDetailsService;
import com.kelly.base.product.identity.auth.dto.PostLoginRequest;
import com.kelly.base.product.identity.domain.account.Account;
import com.kelly.base.product.identity.domain.account.AccountStatus;
import com.kelly.base.product.identity.repository.AccountRepository;
import com.kelly.base.product.identity.response.IdentityResultCode;
import com.kelly.base.product.shared.config.SecurityConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({ AuthService.class, CustomUserDetailsService.class, SecurityConfig.class })
@DisplayName("AuthServiceTests")
class AuthServiceTests {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AuthService authService;

    @Nested
    @DisplayName("LoginTests")
    class LoginTests {
        @ParameterizedTest
        @CsvSource({
                "adv_user, adv_user@password, 82000001",    // 처음 로그인 -> IdentityResultCode.NEED_CHANGE_PASSWORD
                "gen-user, gen-user!password, 200",         // 일반 로그인 -> CommonResultCode.SUCCESS
        })
        @DisplayName("login test - 성공")
        void loginPassTest(final String loginId, final String password, final int expectedCode) {
            // given
            final PostLoginRequest postLoginRequest = new PostLoginRequest(loginId, password);

            // when, then
            final CommonResponse<Void> result = Assertions.assertDoesNotThrow(
                    () -> authService.login(postLoginRequest));

            Assertions.assertEquals(expectedCode, result.getCode());
        }

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
            final PostLoginRequest postLoginRequest = new PostLoginRequest(loginId, password);

            // when, then
            final CommonResponse<Void> result = Assertions.assertDoesNotThrow(
                    () -> authService.login(postLoginRequest));

            Assertions.assertEquals(CommonResultCode.SUCCESS.getCode(), result.getCode());
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
            final PostLoginRequest wrongRequest = new PostLoginRequest(loginId, "wrong_password");

            // when - 비밀번호가 틀렸으므로 BadCredentialsException 이 발생
            final CommonRuntimeException exception1 = Assertions.assertThrows(
                    CommonRuntimeException.class,
                    () -> authService.login(wrongRequest)
            );

            // then - BAD_CREDENTIAL 이 발생했는지 확인
            Assertions.assertEquals(IdentityResultCode.BAD_CREDENTIAL, exception1.getResultCode());

            // then - fail count 가 1 증가했는지 확인
            Assertions.assertEquals(beforeFailCount + 1, account.getFailCount());
        }

        @Test
        @DisplayName("login test - 비밀번호 6번 틀림")
        void loginFail6TimesTest() {
            // 비밀번호가 5번 틀린 계정으로 로그인 검증

            // ------------------------------------------------------------------------------------------------------ //
            // [1] 비밀번호가 1번 더 틀려서 계정이 잠김
            // ------------------------------------------------------------------------------------------------------ //
            // given - 툴린 비밀번호로 로그인 시도
            final String loginId = "failure5";
            final PostLoginRequest wrongRequest = new PostLoginRequest(loginId, "wrong_password");

            // when - 비밀번호가 틀렸으므로 BadCredentialsException 이 발생
            final CommonRuntimeException exception1 = Assertions.assertThrows(
                    CommonRuntimeException.class,
                    () -> authService.login(wrongRequest)
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
            final PostLoginRequest correctRequest = new PostLoginRequest(loginId, loginId);

            // when - 계정이 잠겼으므로 LockedException 이 발생
            final CommonRuntimeException exception2 = Assertions.assertThrows(
                    CommonRuntimeException.class,
                    () -> authService.login(correctRequest)
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
            final PostLoginRequest postLoginRequest = new PostLoginRequest(loginId, loginId);

            // when - INACTIVE 상태이므로 DisabledException 이 발생
            final CommonRuntimeException exception = Assertions.assertThrows(
                    CommonRuntimeException.class,
                    () -> authService.login(postLoginRequest)
            );

            // then - ACCOUNT_DISABLED 이 발생했는지 확인
            Assertions.assertEquals(IdentityResultCode.ACCOUNT_DISABLED, exception.getResultCode());
        }

        @Test
        @DisplayName("login test - 없는 아이디로 로그인 시도")
        void loginFailNotExistTest() {
            // 없는 아이디로 로그인 시도

            // given
            final String loginId = "not_exist";
            final PostLoginRequest postLoginRequest = new PostLoginRequest(loginId, loginId);

            // when, then
            final CommonRuntimeException exception = Assertions.assertThrows(
                    CommonRuntimeException.class,
                    () -> authService.login(postLoginRequest)

            );
            // then - BAD_CREDENTIAL 이 발생했는지 확인
            Assertions.assertEquals(IdentityResultCode.BAD_CREDENTIAL, exception.getResultCode());
        }

        @Test
        @DisplayName("login test - 잠금 해제되어 로그인 성공 (expire date 가 null)")
        void loginPassWithExpireNullTest() {
            // 잠긴 계정인데 expired 시간이 null 인 경우 ( 일반적인 상황에서 발생하지 않는 예외 케이스 )

            // given - lock 상태인데 lockout_expired_at 가 null 인 경우
            final String loginId = "invalid";
            final PostLoginRequest postLoginRequest = new PostLoginRequest(loginId, loginId);

            // when, then
            final CommonResponse<Void> result = Assertions.assertDoesNotThrow(
                    () -> authService.login(postLoginRequest));

            Assertions.assertEquals(CommonResultCode.SUCCESS.getCode(), result.getCode());
        }
   }
}
