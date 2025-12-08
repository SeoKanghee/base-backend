package com.kelly.base.product.identity.accounts;

import com.kelly.base.common.audit.AuditLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.kelly.base.product.shared.Constants.UrlInfo.URI_ROOT_ACCOUNTS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountsController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@DisplayName("AccountsControllerTests")
class AccountsControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountsService accountsService;

    @MockitoBean
    private AuditLogService auditLogService;

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
