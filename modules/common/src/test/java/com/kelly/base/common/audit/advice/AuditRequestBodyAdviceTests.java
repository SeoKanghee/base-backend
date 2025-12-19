package com.kelly.base.common.audit.advice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Type;

import static com.kelly.base.common.CommonConstants.AuditConstants.ATTR_AUDIT_REQ_BODY;
import static org.mockito.Mockito.*;

@DisplayName("AuditRequestBodyAdviceTests")
class AuditRequestBodyAdviceTests {
    private AuditRequestBodyAdvice auditRequestBodyAdvice;

    private MockHttpServletRequest servletRequest;

    @BeforeEach
    void init() {
        servletRequest = new MockHttpServletRequest();
        final ServletRequestAttributes requestAttributes = new ServletRequestAttributes(servletRequest);
        auditRequestBodyAdvice = new AuditRequestBodyAdvice();

        // RequestContextHolder 설정
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    @AfterEach
    void release() {
        // RequestContextHolder 리셋
        RequestContextHolder.resetRequestAttributes();
    }

    @Nested
    @DisplayName("SupportsTests")
    class SupportsTests {
        @Test
        @DisplayName("supports test")
        void supportsTest() {
            // when
            final Boolean result = Assertions.assertDoesNotThrow(
                    () -> auditRequestBodyAdvice.supports(
                            mock(MethodParameter.class), mock(Type.class),
                            JacksonJsonHttpMessageConverter.class
                    )
            );

            // then - 항상 true 반환
            Assertions.assertEquals(Boolean.TRUE, result);
        }
    }

    @Nested
    @DisplayName("AfterBodyReadTests")
    class AfterBodyReadTests {
        @Test
        @DisplayName("afterBodyRead test")
        void afterBodyReadTest() {
            // given
            final String testBody = "test request body";

            // when
            final Object result = Assertions.assertDoesNotThrow(
                    () -> auditRequestBodyAdvice.afterBodyRead(
                            testBody, mock(HttpInputMessage.class), mock(MethodParameter.class), mock(Type.class),
                            JacksonJsonHttpMessageConverter.class
                    )
            );

            // then - return 값 확인
            Assertions.assertNotNull(result);
            Assertions.assertEquals(testBody, result);

            // then - 저장된 attribute 확인
            final String storedBody = (String) servletRequest.getAttribute(ATTR_AUDIT_REQ_BODY);
            Assertions.assertNotNull(storedBody);
            Assertions.assertEquals(testBody, storedBody);
        }
    }
}
