package com.kelly.base.product.identity.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelly.base.common.audit.AuditLogService;
import com.kelly.base.common.config.CommonBeanConfig;
import com.kelly.base.common.config.CommonPropertiesConfig;
import com.kelly.base.product.identity.auth.dto.PostLoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.kelly.base.product.shared.Constants.UrlInfo.URI_ROOT_AUTH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
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
            final PostLoginRequest request = new PostLoginRequest("user1", "password123");

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
        @DisplayName("[post] login test - wrong request")
        void loginWrongReqTest(final String loginId, final String password) throws Exception {
            // given
            final PostLoginRequest request = new PostLoginRequest(loginId, password);

            // when, then
            mockMvc.perform(
                    post(URI_ROOT_AUTH + "/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(request))
            ).andExpect(status().isBadRequest());
        }
    }
}
