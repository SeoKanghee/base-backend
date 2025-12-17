package com.kelly.base.identity.auth;

import com.kelly.base.common.exception.CommonRuntimeException;
import com.kelly.base.common.interfaces.IResultCode;
import com.kelly.base.common.response.CommonResponse;
import com.kelly.base.common.response.CommonResultCode;
import com.kelly.base.common.utils.DateTimeUtil;
import com.kelly.base.identity.auth.dto.PostLoginRequest;
import com.kelly.base.identity.auth.strategy.AuthenticationStrategy;
import com.kelly.base.identity.internal.domain.Account;
import com.kelly.base.identity.internal.domain.repository.AccountRepository;
import com.kelly.base.identity.internal.response.IdentityResultCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

/**
 * authentication service
 *
 * @author 서강희
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final AuthenticationStrategy authenticationStrategy;

    private final AccountRepository accountRepository;

    /**
     * 로그인 처리
     *
     * @param loginRequest   로그인을 위한 정보
     * @param servletRequest HTTP 요청 정보
     * @return HTTP response <code>CommonResultCode</code>, <code>IdentityResultCode</code>
     */
    @Transactional
    public CommonResponse<Void> login(@NonNull final PostLoginRequest loginRequest, HttpServletRequest servletRequest) {
        log.debug("login attempt for account: {}", loginRequest);

        try {
            // 1. 인증 시도 -> 인증 실패할 경우 exception 발생
            Authentication authentication = dispatchAuthentication(loginRequest);

            // 2. 인증 성공 처리 : 200
            return handleSuccessfulLogin(authentication, loginRequest, servletRequest);
        } catch (LockedException e) {
            // 계정이 잠겨 있음 : 403
            log.error("account locked: {}", loginRequest.loginId());
            throw new CommonRuntimeException(IdentityResultCode.ACCOUNT_LOCKED);
        } catch (DisabledException e) {
            // 계정이 비활성화 되어 있음 : 403
            log.error("account disabled: {}", loginRequest.loginId());
            throw new CommonRuntimeException(IdentityResultCode.ACCOUNT_DISABLED);
        } catch (BadCredentialsException e) {
            // 인증 실패 ( 비밀번호 틀림 ) : 401
            handleFailedLogin(loginRequest.loginId());
            log.error("invalid credentials : {}", loginRequest.loginId());
            throw new CommonRuntimeException(IdentityResultCode.BAD_CREDENTIAL);
        } catch (InternalAuthenticationServiceException e) {
            // 내부에서 사용하는 exception 인 경우
            if (e.getCause() instanceof CommonRuntimeException commonRuntimeException) {
                throw commonRuntimeException;
            }
            throw e;    // 그 외에는 InternalAuthenticationServiceException 을 그대로 처리
        }
    }

    Authentication dispatchAuthentication(final PostLoginRequest loginRequest) {
        // AuthenticationManager 를 통해 계정 정보 확인
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.loginId(), loginRequest.password())
        );
    }

    CommonResponse<Void> handleSuccessfulLogin(
            final Authentication authentication, final PostLoginRequest loginRequest,
            HttpServletRequest servletRequest
    ) {
        // 1. exception 이 발생하지 않은 경우에 대한 로그인 처리
        final String loginId = loginRequest.loginId();
        final IResultCode resultCode = getLoginResultCode(loginId);
        log.info("result code: {}, loginId: {}", resultCode, loginId);

        // 2. 인증 전략에 따른 세션 유지 방법 적용
        authenticationStrategy.handleLogin(authentication, loginRequest, servletRequest);

        return new CommonResponse<>(resultCode);
    }

    private IResultCode getLoginResultCode(final String loginId) {
        // 계정에 대한 유효성은 AuthenticationManager 에서 처리했으므로 orElse 는 의미 없음
        final Account account = accountRepository.findByLoginId(loginId).orElse(Account.builder().build());

        // 로그인이 성공한 상황이므로 쌓여있던 fail count 는 초기화
        account.initFailCount();

        if (account.isFirstLogin()) {
            // 비번을 바꿔야 하는 경우이므로 로그인 시간을 따로 기록하지 않음
            return IdentityResultCode.NEED_CHANGE_PASSWORD;
        } else {
            account.recordLoginTimestamp(DateTimeUtil.nowUtc());
            return CommonResultCode.SUCCESS;
        }
    }

    void handleFailedLogin(final String loginId) {
        accountRepository.findByLoginId(loginId).ifPresent(this::changeStatusByFailedLogin);
    }

    private void changeStatusByFailedLogin(final Account account) {
        final long currentFailCount = account.getFailCount() + 1;
        if (currentFailCount >= 6) {
            // 로그인 실패 횟수가 임계치를 넘겼을 경우 계정 잠금
            final ZonedDateTime lockedExpiredTime = DateTimeUtil.nowUtcPlusMinutes(30L);
            log.error("account will be locked: lockout expire time ~ {}", lockedExpiredTime);
            account.lockAccount(lockedExpiredTime);
        } else {
            // 로그인 실패 횟수 증가
            log.debug("increase fail count: {}", currentFailCount);
            account.increaseFailCount();
        }
    }

    /**
     * 로그아웃 처리
     *
     * @param servletRequest HTTP 요청 정보
     * @return HTTP response <code>CommonResultCode</code>
     */
    public CommonResponse<Void> logout(HttpServletRequest servletRequest) {
        authenticationStrategy.handleLogout(servletRequest);
        return new CommonResponse<>(CommonResultCode.SUCCESS);
    }
}
