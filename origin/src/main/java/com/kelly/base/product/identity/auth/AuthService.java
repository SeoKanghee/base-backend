package com.kelly.base.product.identity.auth;

import com.kelly.base.common.exception.CommonRuntimeException;
import com.kelly.base.common.interfaces.IResultCode;
import com.kelly.base.common.response.CommonResponse;
import com.kelly.base.common.response.CommonResultCode;
import com.kelly.base.common.utils.DateTimeUtil;
import com.kelly.base.product.identity.adapter.security.CustomUserDetails;
import com.kelly.base.product.identity.auth.dto.PostLoginRequest;
import com.kelly.base.product.identity.domain.account.Account;
import com.kelly.base.product.identity.repository.AccountRepository;
import com.kelly.base.product.identity.response.IdentityResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;

    private final AccountRepository accountRepository;

    @Transactional
    public CommonResponse<Void> login(@NonNull final PostLoginRequest loginRequest) {
        log.debug("login attempt for user: {}", loginRequest);

        try {
            // 1. 인증 시도 -> 인증 실패할 경우 exception 발생
            Authentication authentication = dispatchAuthentication(loginRequest);

            // 2. 인증 성공 처리 : 200
            return handleSuccessfulLogin(authentication, loginRequest.loginId());
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
            log.error("invalid credentials for user: {}", loginRequest.loginId());
            throw new CommonRuntimeException(IdentityResultCode.BAD_CREDENTIAL);
        }
    }

    private Authentication dispatchAuthentication(final PostLoginRequest loginRequest) {
        // AuthenticationManager 를 통해 계정 정보 확인
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.loginId(), loginRequest.password())
        );
    }

    CommonResponse<Void> handleSuccessfulLogin(final Authentication authentication, final String loginId) {
        final IResultCode resultCode = getLoginResultCode(loginId);
        log.info("result code: {}, loginId: {}", resultCode, loginId);

        // SecurityContext에 저장 (세션 생성)
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return new CommonResponse<>(resultCode);
    }

    private IResultCode getLoginResultCode(final String loginId) {
        // 계정에 대한 유효성은 AuthenticationManager 에서 처리했으므로 orElse 는 의미 없음
        final Account account = accountRepository.findByLoginId(loginId).orElse(Account.builder().build());

        // 로그인이 성공한 상황이므로 쌓여있던 fail count 는 초기화 후 ResultCode 판단해서 반환
        account.initFailCount();
        return account.isFirstLogin() ? IdentityResultCode.NEED_CHANGE_PASSWORD : CommonResultCode.SUCCESS;
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
}
