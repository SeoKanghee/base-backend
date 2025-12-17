package com.kelly.base.identity.adapter.audit;

import com.kelly.base.common.interfaces.IAuditContextProvider;
import com.kelly.base.common.config.CommonBeanConfig;
import com.kelly.base.common.utils.DateTimeUtil;
import com.kelly.base.identity.adapter.audit.SecurityAuditContextProvider;
import com.kelly.base.identity.adapter.security.CustomUserDetails;
import com.kelly.base.identity.domain.account.Account;
import com.kelly.base.identity.domain.account.AccountStatus;
import com.kelly.base.identity.domain.permission.Permission;
import com.kelly.base.identity.domain.role.Role;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

@DisplayName("SecurityAuditContextProviderTests")
class SecurityAuditContextProviderTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Nested
    @DisplayName("InjectionTests")
    class InjectionTests {
        @Test
        @DisplayName("injection test - SecurityAuditContextProvider가 없을 때")
        void injectionWithoutSecurityTest() {
            // when
            contextRunner.withUserConfiguration(
                    CommonBeanConfig.class
            ).run(
                    context -> {
                        // then - DefaultAuditContextProvider가 주입됐는지 확인
                        final IAuditContextProvider auditContextProvider = context.getBean(IAuditContextProvider.class);
                        Assertions.assertNotNull(auditContextProvider);
                        Assertions.assertNull(auditContextProvider.getDetailedInfo());
                    }
            );
        }

        @Test
        @DisplayName("injection test - SecurityAuditContextProvider가 있을 때")
        void injectionWithSecurityTest() {
            // when
            contextRunner.withUserConfiguration(
                    SecurityAuditContextProvider.class,
                    CommonBeanConfig.class
            ).run(
                    context -> {
                        // then - SecurityAuditContextProvider가 주입됐는지 확인
                        final IAuditContextProvider auditContextProvider = context.getBean(IAuditContextProvider.class);
                        Assertions.assertNotNull(auditContextProvider);
                        Assertions.assertInstanceOf(SecurityAuditContextProvider.class, auditContextProvider);
                        Assertions.assertNull(auditContextProvider.getDetailedInfo());
                    }
            );
        }
    }

    @Nested
    @DisplayName("GetDetailedInfoTests")
    class GetDetailedInfoTests {
        private SecurityAuditContextProvider auditContextProvider;

        @BeforeEach
        void init() {
            auditContextProvider = new SecurityAuditContextProvider();
            SecurityContextHolder.clearContext();
        }

        @AfterEach
        void release() {
            SecurityContextHolder.clearContext();
        }

        @Test
        @DisplayName("getDetailedInfo test - 인증 정보가 CustomUserDetails 인 경우")
        void getDetailedInfoWithCustomUserDetailsTest() {
            // given - CustomUserDetails 생성
            final Authentication authentication = getAuthenticationByCustomUserDetails();
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // when
            final String result = auditContextProvider.getDetailedInfo();

            // then - 예상되는 형식으로 반환되는지 확인
            Assertions.assertNotNull(result);
            Assertions.assertEquals("userId: testLoginId, accountId: 999, role: ROLE_SITE_MANAGER", result);
        }

        private Authentication getAuthenticationByCustomUserDetails() {
            final Account account = Account.builder().id(999L).loginId("testLoginId").password("password")
                                           .name("테스트사용자").role("ROLE_SITE_MANAGER").status(AccountStatus.ACTIVE)
                                           .passwordExpiredAt(DateTimeUtil.nowUtcPlusMinutes(60)).build();
            final Role role = Role.builder().id(1L).code("ROLE_SITE_MANAGER").name("사이트 관리자").build();
            final Permission permission = Permission.builder().id(1L).code("MANAGE_ACCOUNT").name("계정 관리").build();
            final CustomUserDetails userDetails = new CustomUserDetails(account, role, Set.of(permission));

            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        }

        @Test
        @DisplayName("getDetailedInfo test - 인증 정보가 null 인 경우")
        void getDetailedInfoWithNullAuthenticationTest() {
            // given - SecurityContext에 authentication이 설정되지 않음 (null)

            // when
            final String result = auditContextProvider.getDetailedInfo();

            // then
            Assertions.assertNull(result);
        }

        @Test
        @DisplayName("getDetailedInfo test - 인증 정보가 CustomUserDetails 가 아닌 경우")
        void getDetailedInfoWithNonCustomUserDetailsTest() {
            // given - String 타입의 principal 사용
            final Authentication authentication = new UsernamePasswordAuthenticationToken(
                    "anonymousUser",
                    null,
                    null
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // when
            final String result = auditContextProvider.getDetailedInfo();

            // then
            Assertions.assertNull(result);
        }
    }
}
