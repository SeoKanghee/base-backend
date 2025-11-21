package com.kelly.base.product.identity.adapter.security;

import com.kelly.base.common.exception.CommonRuntimeException;
import com.kelly.base.common.response.CommonResultCode;
import com.kelly.base.common.utils.ConvertUtil;
import com.kelly.base.common.utils.DateTimeUtil;
import com.kelly.base.product.identity.domain.account.Account;
import com.kelly.base.product.identity.domain.account.AccountStatus;
import com.kelly.base.product.identity.domain.permission.Permission;
import com.kelly.base.product.identity.domain.role.Role;
import com.kelly.base.product.identity.repository.AccountRepository;
import com.kelly.base.product.identity.repository.PermissionRepository;
import com.kelly.base.product.identity.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Set;

/**
 * 계정 정보를 database 로 부터 읽어서 Spring Security 에 전달하기 위한 서비스<p>
 * 흐름 정의 : Spring Security <-> CustomUserDetailsService <-> DataBase
 *
 * @author 서강희
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    /**
     * loginId 를 이용해 계정 정보를 읽어옴
     *
     * @param loginId 로그인에 필요한 ID
     * @return 계정 정보
     * @throws UsernameNotFoundException 사용자를 못찾을 경우
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String loginId) {
        // AuthenticationManager 의 authenticate 호출 시 계정 정보를 읽기 위해 호출
        final Account account = getAccountInfo(loginId);
        log.debug("found account: {}", account);

        // 최종 인증 처리 전 상태 확인
        changeStatusTryToLogin(account);

        // Role 조회 (account.role은 role.code 값)
        final Role role = getRole(account.getRole());

        // Role에 연결된 Permissions 조회
        final Set<Permission> permissions = permissionRepository.findPermissionSetByRoleId(role.getId());
        log.debug("loaded role: {}, permissions count: {}", role.getCode(), permissions.size());

        return new CustomUserDetails(account, role, permissions);
    }

    Account getAccountInfo(final String loginId) {
        // DB 에서 사용자 정보를 찾아서 반환
        final String lowercaseLoginId = ConvertUtil.toLowerCase(loginId);   // 소문자로 변환
        log.debug("loading user by loginId(lowercase): {}", lowercaseLoginId);

        // authService 에서 BadCredentialsException 으로 처리하기 위해 UsernameNotFoundException 으로 throw
        return accountRepository.findByLoginId(lowercaseLoginId)
                                .orElseThrow(
                                        () -> {
                                            final String errMsg = "user not found: " + loginId;
                                            log.error(errMsg);
                                            return new UsernameNotFoundException(errMsg);
                                        }
                                );
    }

    void changeStatusTryToLogin(final Account account) {
        // 계정 잠금 만료된 상태
        if (account.getStatus() == AccountStatus.LOCKED
                && isExpired(account.getLockoutExpiredAt())) {
            log.info("account has been unlocked: {}", account.getLoginId());
            account.unlockAccount();    // 계정 장금을 해제해서 로그인을 시도할 수 있게 수정
        }
    }

    private boolean isExpired(final ZonedDateTime lockoutExpireTime) {
        // 그럴일은 없어야 겠지만 LOCKED 상태인데 null 인 경우 회피
        return lockoutExpireTime == null || lockoutExpireTime.isBefore(DateTimeUtil.nowUtc());
    }

    Role getRole(final String roleCode) {
        return roleRepository.findByCode(roleCode).orElseThrow(
                () -> new CommonRuntimeException(CommonResultCode.DATABASE_ERROR, "role info cannot be found")
        );
    }
}
