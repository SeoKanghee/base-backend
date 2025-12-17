package com.kelly.base.core.system;

import com.kelly.base.common.audit.AuditLogService;
import com.kelly.base.common.exception.CommonExceptionHandler;
import com.kelly.base.common.i18n.I18nProperties;
import com.kelly.base.common.interfaces.II18nMessageService;
import com.kelly.base.identity.PermissionCheckAspect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.kelly.base.common.CommonConstants.UrlInfo.URI_ROOT_SYSTEM;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SystemController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@EnableConfigurationProperties({ I18nProperties.class, SecurityProperties.class })  // interceptor 처리시 필요
@AutoConfigureMockMvc(addFilters = false)   // bean 만 생성하고, security filter 는 비활성화
@Import({PermissionCheckAspect.class, CommonExceptionHandler.class })   // permission aspect 및 http status 처리
@EnableAspectJAutoProxy
@DisplayName("SystemControllerTests")
class SystemControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditLogService auditLogService;

    @MockitoBean
    private II18nMessageService i18nMessageService;

    @AfterEach
    void clearContext() {
        // permission 설정 정리
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("reloadMessagesTests")
    class ReloadMessagesTests {
        private final String testUri = URI_ROOT_SYSTEM + "/i18n/reload";

        @Test
        @DisplayName("[post] reloadMessages test - 권한이 있는 경우")
        void reloadMessagesWithPermissionTest() throws Exception {
            // given - authentication
            final Authentication authentication = new UsernamePasswordAuthenticationToken(
                    "testUser",
                    "password",
                    List.of(new SimpleGrantedAuthority("MANAGE_SYSTEM"))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // when, then
            mockMvc.perform(post(testUri)).andExpect(status().isOk());   // 200 OK
        }

        @Test
        @DisplayName("[post] reloadMessages test - 권한이 없는 경우")
        void reloadMessagesWithoutPermissionTest() throws Exception {
            // given - authentication
            final Authentication authentication = new UsernamePasswordAuthenticationToken(
                    "testUser",
                    "password",
                    List.of(new SimpleGrantedAuthority("MANAGE_MY_ACCOUNT"))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // when, then
            mockMvc.perform(post(testUri)).andExpect(status().isForbidden());    // 403 forbidden
        }

        @Test
        @DisplayName("[post] reloadMessages test - 인증 정보가 없는 경우")
        void reloadMessagesUnAuthTest() throws Exception {
            // when, then
            mockMvc.perform(post(testUri)).andExpect(status().isUnauthorized()); // 401 unauthorized
        }
    }
}