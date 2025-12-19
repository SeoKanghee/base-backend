package com.kelly.base.identity.accounts;

import com.kelly.base.common.audit.AuditLogService;
import com.kelly.base.common.i18n.I18nProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.kelly.base.identity.internal.Constants.UrlInfo.URI_ROOT_ACCOUNTS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountsController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@EnableConfigurationProperties({ I18nProperties.class, SecurityProperties.class })  // interceptor 처리시 필요
@AutoConfigureMockMvc(addFilters = false)   // bean 만 생성하고, security filter 는 비활성화
@DisplayName("AccountsControllerTests")
class AccountsControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditLogService auditLogService;

    @MockitoBean
    private AccountsService accountsService;

    @Nested
    @DisplayName("GetRetrieveTests")
    class GetRetrieveTests {
        @Test
        @DisplayName("[get] retrieve test - 200 OK")
        void retrieve200OKTest() throws Exception {
            // when, then
            mockMvc.perform(
                    get(URI_ROOT_ACCOUNTS)
            ).andExpect(status().isOk());
        }
    }
}
