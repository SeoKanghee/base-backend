package com.kelly.base.identity.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelly.base.common.audit.AuditLogService;
import com.kelly.base.common.config.CommonBeanConfig;
import com.kelly.base.common.config.CommonPropertiesConfig;
import com.kelly.base.common.i18n.I18nProperties;
import com.kelly.base.identity.auth.AuthController;
import com.kelly.base.identity.auth.AuthService;
import com.kelly.base.identity.auth.dto.PostLoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.kelly.base.common.CommonConstants.UrlInfo.URI_ROOT_AUTH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@EnableConfigurationProperties({ I18nProperties.class, SecurityProperties.class })  // interceptor 처리시 필요
@AutoConfigureMockMvc(addFilters = false)   // bean 만 생성하고, security filter 는 비활성화
@Import(CommonBeanConfig.class)
@DisplayName("AuthControllerTests")
class AuthControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommonPropertiesConfig commonPropertiesConfig;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private AuditLogService auditLogService;

    @Nested
    @DisplayName("PostLoginTests")
    class PostLoginTests {
        @Test
        @DisplayName("[post] login test - 200 OK")
        void login200OKTest() throws Exception {
            // given
            final PostLoginRequest request = new PostLoginRequest("user1", "password123", false);

            // when, then
            mockMvc.perform(
                    post(URI_ROOT_AUTH + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
            ).andExpect(status().isOk());
        }

        @ParameterizedTest
        @CsvSource({
                ", id_is_null",     // loginId 가 null
                "'', id_is_empty",  // loginId 가 빈 문자열
                "pw_is_null,",      // password 가 null
                "pw_is_empty, ''",  // password 가 빈 문자열
        })
        @DisplayName("[post] login test - payload 에 문제가 있는 경우")
        void loginWrongReqTest(final String loginId, final String password) throws Exception {
            // given
            final PostLoginRequest request = new PostLoginRequest(loginId, password, false);

            // when, then
            mockMvc.perform(
                    post(URI_ROOT_AUTH + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
            ).andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PostLogoutTests")
    class PostLogoutTests {
        @Test
        @DisplayName("[post] logout test - 200 OK")
        void logout200OKTest() throws Exception {
            // when, then
            mockMvc.perform(
                    post(URI_ROOT_AUTH + "/logout")
            ).andExpect(status().isOk());
        }
    }
}
