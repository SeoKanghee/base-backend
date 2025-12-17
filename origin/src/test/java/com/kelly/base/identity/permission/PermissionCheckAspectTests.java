package com.kelly.base.identity.permission;

import com.kelly.base.common.exception.CommonRuntimeException;
import com.kelly.base.identity.permission.PermissionCheckAspect;
import com.kelly.base.identity.permission.annotation.PermOperator;
import com.kelly.base.identity.permission.annotation.RequirePermission;
import com.kelly.base.identity.response.IdentityResultCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

import static com.kelly.base.common.CommonConstants.PermissionCode.*;
import static org.mockito.Mockito.*;

@DisplayName("PermissionCheckAspectTests")
class PermissionCheckAspectTests {

    private PermissionCheckAspect permissionCheckAspect;

    @BeforeEach
    void init() {
        permissionCheckAspect = new PermissionCheckAspect();
    }

    @AfterEach
    void release() {
        // SecurityContext 초기화
        SecurityContextHolder.clearContext();
    }

    // test 용 controller
    static class TestController {
        @RequirePermission(MANAGE_MY_ACCOUNT)
        public void apiForGeneralUser() { /* implementation is not required */ }

        @RequirePermission(value = { MANAGE_ACCOUNT, MANAGE_SYSTEM })
        public void apiForServiceEngineer() { /* implementation is not required */ }

        @RequirePermission(value = { MANAGE_ACCOUNT, VIEW_ACCOUNT_LIST }, operator = PermOperator.AND)
        public void apiForPermOperAnd() { /* implementation is not required */ }
    }

    // Authentication Mock 객체 생성
    private Authentication createMockAuthentication(
            final boolean isAuthenticated, final String username,
            final Collection<? extends GrantedAuthority> authorities
    ) {
        final Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(isAuthenticated);
        when(authentication.getName()).thenReturn(username);
        doReturn(authorities).when(authentication).getAuthorities();
        return authentication;
    }

    // SecurityContext 설정
    private void setSecurityContext(final Authentication authentication) {
        final SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Nested
    @DisplayName("RequirePermissionTests")
    class RequirePermissionTests {
        @Test
        @DisplayName("requirePermission annotation test - 권한 체크 성공")
        void requirePermissionSinglePermissionSuccessTest() throws NoSuchMethodException {
            // given
            final Authentication authentication = createMockAuthentication(
                    true,
                    "generalUser",
                    Set.of(
                            new SimpleGrantedAuthority(MANAGE_MY_ACCOUNT)
                    )
            );
            setSecurityContext(authentication);

            final Method method = TestController.class.getMethod("apiForGeneralUser");
            final RequirePermission annotation = method.getAnnotation(RequirePermission.class);

            // when, then
            Assertions.assertDoesNotThrow(
                    () -> permissionCheckAspect.checkPermission(annotation)
            );
        }

        @Test
        @DisplayName("requirePermission annotation test - 권한 체크 실패")
        void requirePermissionSinglePermissionFailTest() throws NoSuchMethodException {
            // given
            final Authentication authentication = createMockAuthentication(
                    true,
                    "generalUser",
                    Set.of(
                            new SimpleGrantedAuthority(VIEW_ACCOUNT_LIST)
                    )
            );
            setSecurityContext(authentication);

            final Method method = TestController.class.getMethod("apiForGeneralUser");
            final RequirePermission annotation = method.getAnnotation(RequirePermission.class);

            // when
            final CommonRuntimeException exception = Assertions.assertThrows(
                    CommonRuntimeException.class,
                    () -> permissionCheckAspect.checkPermission(annotation)
            );

            // then
            Assertions.assertEquals(IdentityResultCode.NO_PERMISSION, exception.getResultCode());
        }

        @Test
        @DisplayName("RequirePermission annotation test - 인증 정보가 없는 경우 AUTH_REQUIRED 예외 발생")
        void requirePermissionNoAuthTest() throws NoSuchMethodException {
            // given
            setSecurityContext(null);

            final Method method = TestController.class.getMethod("apiForGeneralUser");
            final RequirePermission annotation = method.getAnnotation(RequirePermission.class);

            // when
            final CommonRuntimeException exception = Assertions.assertThrows(
                    CommonRuntimeException.class,
                    () -> permissionCheckAspect.checkPermission(annotation)
            );

            // then
            Assertions.assertEquals(IdentityResultCode.AUTH_REQUIRED, exception.getResultCode());
        }

        @Test
        @DisplayName("@RequirePermission annotation test - OR 연산자 권한 확인 성공")
        void requirePermissionOrOperatorSuccessTest() throws NoSuchMethodException {
            // given
            final Authentication authentication = createMockAuthentication(
                    true,
                    "testUser",
                    Set.of(
                            new SimpleGrantedAuthority(MANAGE_ACCOUNT)
                    )
            );
            setSecurityContext(authentication);

            final Method method = TestController.class.getMethod("apiForServiceEngineer");
            final RequirePermission annotation = method.getAnnotation(RequirePermission.class);

            // when, then
            Assertions.assertDoesNotThrow(
                    () -> permissionCheckAspect.checkPermission(annotation)
            );
        }

        @Test
        @DisplayName("requirePermission annotation test - OR 연산자 권한 확인 실패")
        void requirePermissionOrOperatorFailTest() throws NoSuchMethodException {
            // given
            final Authentication authentication = createMockAuthentication(
                    true,
                    "testUser",
                    Set.of(
                            new SimpleGrantedAuthority(MANAGE_MY_ACCOUNT)
                    )
            );
            setSecurityContext(authentication);

            final Method method = TestController.class.getMethod("apiForServiceEngineer");
            final RequirePermission annotation = method.getAnnotation(RequirePermission.class);

            // when
            final CommonRuntimeException exception = Assertions.assertThrows(
                    CommonRuntimeException.class,
                    () -> permissionCheckAspect.checkPermission(annotation)
            );

            // then
            Assertions.assertEquals(IdentityResultCode.NO_PERMISSION, exception.getResultCode());
        }

        @Test
        @DisplayName("requirePermission annotation test - AND 연산자 권한 확인 성공")
        void requirePermissionAndOperatorSuccessTest() throws NoSuchMethodException {
            // given
            final Authentication authentication = createMockAuthentication(
                    true,
                    "testUser",
                    Set.of(
                            new SimpleGrantedAuthority(MANAGE_ACCOUNT),
                            new SimpleGrantedAuthority(VIEW_ACCOUNT_LIST)
                    )
            );
            setSecurityContext(authentication);

            final Method method = TestController.class.getMethod("apiForPermOperAnd");
            final RequirePermission annotation = method.getAnnotation(RequirePermission.class);

            // when, then
            Assertions.assertDoesNotThrow(
                    () -> permissionCheckAspect.checkPermission(annotation)
            );
        }

        @Test
        @DisplayName("requirePermission annotation test - AND 연산자 권한 확인 실패")
        void requirePermissionAndOperatorPartialFailTest() throws NoSuchMethodException {
            // given
            final Authentication authentication = createMockAuthentication(
                    true,
                    "testUser",
                    Set.of(
                            new SimpleGrantedAuthority(MANAGE_ACCOUNT)
                    )
            );
            setSecurityContext(authentication);

            final Method method = TestController.class.getMethod("apiForPermOperAnd");
            final RequirePermission annotation = method.getAnnotation(RequirePermission.class);

            // when
            final CommonRuntimeException exception = Assertions.assertThrows(
                    CommonRuntimeException.class,
                    () -> permissionCheckAspect.checkPermission(annotation)
            );

            // then
            Assertions.assertEquals(IdentityResultCode.NO_PERMISSION, exception.getResultCode());
        }
    }

    @Nested
    @DisplayName("CheckAuthenticationTests")
    class CheckAuthenticationTests {
        @Test
        @DisplayName("checkAuthentication test - 인증 정보가 null인 경우 AUTH_REQUIRED 예외 발생 확인")
        void checkAuthenticationNullTest() {
            // when
            final CommonRuntimeException exception = Assertions.assertThrows(
                    CommonRuntimeException.class,
                    () -> permissionCheckAspect.checkAuthentication(null)
            );

            // then
            Assertions.assertEquals(IdentityResultCode.AUTH_REQUIRED, exception.getResultCode());
        }

        @Test
        @DisplayName("checkAuthentication test - 인증되지 않은 경우 AUTH_REQUIRED 예외 발생 확인")
        void checkAuthenticationNotAuthenticatedTest() {
            // given
            final Authentication authentication
                    = createMockAuthentication(false, "testUser", Set.of());

            // when
            final CommonRuntimeException exception = Assertions.assertThrows(
                    CommonRuntimeException.class,
                    () -> permissionCheckAspect.checkAuthentication(authentication)
            );

            // then
            Assertions.assertEquals(IdentityResultCode.AUTH_REQUIRED, exception.getResultCode());
        }

        @Test
        @DisplayName("checkAuthentication test - 정상 인증된 경우")
        void checkAuthenticationSuccessTest() {
            // given
            final Authentication authentication = createMockAuthentication(
                    true,
                    "testUser",
                    Set.of(new SimpleGrantedAuthority(MANAGE_MY_ACCOUNT))
            );

            // when, then
            Assertions.assertDoesNotThrow(
                    () -> permissionCheckAspect.checkAuthentication(authentication)
            );
        }
    }

    @Nested
    @DisplayName("CheckPermissionsTests")
    class CheckPermissionsTests {
        @ParameterizedTest
        @MethodSource("provideOrOperatorTestCases")
        @DisplayName("checkPermissions test - OR 연산자 권한 확인")
        void checkPermissionsOrOperatorTest(
                final Set<String> userAuthorities, final String[] requiredPermissions, final boolean expectedResult
        ) {
            // when
            final boolean result = permissionCheckAspect.checkPermissions(
                    userAuthorities,
                    requiredPermissions,
                    PermOperator.OR
            );

            // then
            Assertions.assertEquals(expectedResult, result);
        }

        static Stream<Arguments> provideOrOperatorTestCases() {
            return Stream.of(
                    // 필요한 권한 중 하나를 가진 경우
                    Arguments.of(
                            Set.of(MANAGE_MY_ACCOUNT),
                            new String[]{ MANAGE_MY_ACCOUNT, MANAGE_ACCOUNT },
                            true
                    ),
                    // 필요한 권한 모두를 가진 경우
                    Arguments.of(
                            Set.of(MANAGE_ACCOUNT, MANAGE_SYSTEM),
                            new String[]{ MANAGE_ACCOUNT, MANAGE_SYSTEM },
                            true
                    ),
                    // 필요한 권한이 없는 경우
                    Arguments.of(
                            Set.of(MANAGE_MY_ACCOUNT),
                            new String[]{ MANAGE_ACCOUNT, MANAGE_SYSTEM },
                            false
                    ),
                    // 권한이 전혀 없는 경우
                    Arguments.of(
                            Set.of(),
                            new String[]{ MANAGE_ACCOUNT },
                            false
                    )
            );
        }

        @ParameterizedTest
        @MethodSource("provideAndOperatorTestCases")
        @DisplayName("checkPermissions test - AND 연산자 권한 확인")
        void checkPermissionsAndOperatorTest(
                final Set<String> userAuthorities, final String[] requiredPermissions, final boolean expectedResult
        ) {
            // when
            final boolean result = permissionCheckAspect.checkPermissions(
                    userAuthorities,
                    requiredPermissions,
                    PermOperator.AND
            );

            // then
            Assertions.assertEquals(expectedResult, result);
        }

        static Stream<Arguments> provideAndOperatorTestCases() {
            return Stream.of(
                    // 모든 필요한 권한을 가진 경우
                    Arguments.of(
                            Set.of(MANAGE_ACCOUNT, VIEW_ACCOUNT_LIST),
                            new String[]{ MANAGE_ACCOUNT, VIEW_ACCOUNT_LIST },
                            true
                    ),
                    // 필요한 권한보다 더 많이 가진 경우
                    Arguments.of(
                            Set.of(MANAGE_ACCOUNT, VIEW_ACCOUNT_LIST, MANAGE_SYSTEM),
                            new String[]{ MANAGE_ACCOUNT, VIEW_ACCOUNT_LIST },
                            true
                    ),
                    // 일부 권한만 가진 경우
                    Arguments.of(
                            Set.of(MANAGE_ACCOUNT),
                            new String[]{ MANAGE_ACCOUNT, VIEW_ACCOUNT_LIST },
                            false
                    ),
                    // 아무 권한도 없는 경우
                    Arguments.of(
                            Set.of(),
                            new String[]{ MANAGE_ACCOUNT, VIEW_ACCOUNT_LIST },
                            false
                    )
            );
        }
    }
}
