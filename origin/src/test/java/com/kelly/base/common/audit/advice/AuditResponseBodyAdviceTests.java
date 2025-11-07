package com.kelly.base.common.audit.advice;

import com.kelly.base.common.audit.AuditLogService;
import com.kelly.base.common.audit.annotation.NoAudit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Map;

import static com.kelly.base.common.CommonConstants.AuditConstants.*;
import static com.kelly.base.common.audit.advice.AuditResponseBodyAdvice.*;
import static org.mockito.Mockito.*;

@DisplayName("AuditResponseBodyAdviceTests")
class AuditResponseBodyAdviceTests {
    private AuditLogService mockAuditLogService;

    private AuditResponseBodyAdvice auditResponseBodyAdvice;

    private MockHttpServletRequest servletRequest;

    @BeforeEach
    void init() {
        servletRequest = new MockHttpServletRequest();
        final ServletRequestAttributes requestAttributes = new ServletRequestAttributes(servletRequest);

        // mocking 된 객체들로 초기화
        mockAuditLogService = mock(AuditLogService.class);
        auditResponseBodyAdvice = new AuditResponseBodyAdvice(mockAuditLogService);

        // RequestContextHolder 설정
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    @AfterEach
    void release() {
        // RequestContextHolder 리셋
        RequestContextHolder.resetRequestAttributes();
    }

    // test 용 controller - method NoAudit
    static class TestController {
        public void auditMethod() { /* implementation is not required */ }

        @NoAudit
        public void noAuditMethod() { /* implementation is not required */ }
    }

    // test 용 controller - class NoAudit
    @NoAudit
    static class NoAuditTestController {
        public void innerMethod() { /* implementation is not required */ }
    }

    @Nested
    @DisplayName("SupportsTests")
    class SupportsTests {
        @ParameterizedTest
        @CsvSource({
                "auditMethod, true",
                "noAuditMethod, false",
        })
        @DisplayName("supports test - normal controller")
        void supportsNormalControllerTest(final String methodName,
                                          final boolean expectedResult) throws NoSuchMethodException {
            // given
            final Method method = TestController.class.getMethod(methodName);
            final MethodParameter methodParameter = new MethodParameter(method, -1);

            // when
            final boolean result = Assertions.assertDoesNotThrow(
                    () -> auditResponseBodyAdvice.supports(
                            methodParameter,
                            MappingJackson2HttpMessageConverter.class
                    )
            );

            // then
            Assertions.assertEquals(expectedResult, result);
        }

        @Test
        @DisplayName("supports test - no audit controller")
        void supportsNoAuditControllerTest() throws NoSuchMethodException {
            // given
            final Method method = NoAuditTestController.class.getMethod("innerMethod");
            final MethodParameter methodParameter = new MethodParameter(method, -1);

            // when
            final boolean result = Assertions.assertDoesNotThrow(
                    () -> auditResponseBodyAdvice.supports(
                            methodParameter,
                            MappingJackson2HttpMessageConverter.class
                    )
            );

            // then - class 의 @NoAudit 이 내부 method 에도 영향을 주는지 확인
            Assertions.assertFalse(result);
        }
    }

    @Nested
    @DisplayName("BeforeBodyWriteTests")
    class BeforeBodyWriteTests {
        void setRequest(final String methodType, final String uri, final String ip, final String query,
                        final String requestBody) {
            servletRequest.setMethod(methodType);
            servletRequest.setRequestURI(uri);
            servletRequest.setRemoteAddr(ip);
            servletRequest.setQueryString(query);
            servletRequest.setAttribute(ATTR_AUDIT_REQ_BODY, requestBody);
        }

        @Test
        @DisplayName("beforeBodyWrite test - excluded uri")
        void beforeBodyWriteExcludedTest() throws NoSuchMethodException {
            // given
            setRequest("GET", "/swagger-ui/index.html", "192.168.1.119", null, null);
            final String responseBody = "response body";

            Method method = TestController.class.getMethod("auditMethod");
            MethodParameter methodParameter = new MethodParameter(method, -1);

            // when
            final Object result = Assertions.assertDoesNotThrow(
                    () -> auditResponseBodyAdvice.beforeBodyWrite(
                            responseBody,
                            methodParameter,
                            MediaType.APPLICATION_JSON,
                            JsonbHttpMessageConverter.class,
                            new ServletServerHttpRequest(servletRequest),
                            mock(ServerHttpResponse.class)
                    )
            );

            // then - body 반환 확인
            Assertions.assertEquals(responseBody, result);

            // then - auditLogService.logApiCall 이 호출되지 않았는지 확인
            verify(mockAuditLogService, never()).logApiCall(anyString(), anyString(), anyMap());
        }

        @ParameterizedTest
        @CsvSource({
                // 모든 데이터가 있는 경우
                "POST, /api/test, 192.168.1.119, param1=value1&param2=value2, test request, test response",
                // query, request, response 데이터가 없는 경우
                "GET, /api/test, 192.168.1.119, , , ",
        })
        @DisplayName("beforeBodyWrite test - with all data")
        void beforeBodyWriteAllDataTest(
                final String methodType, final String uri, final String ip, final String query,
                final String requestBody, final String responseBody
        ) throws NoSuchMethodException {
            // given
            setRequest(methodType, uri, ip, query, requestBody);

            Method method = TestController.class.getMethod("auditMethod");
            MethodParameter methodParameter = new MethodParameter(method, -1);

            // when
            final Object result = Assertions.assertDoesNotThrow(
                    () -> auditResponseBodyAdvice.beforeBodyWrite(
                            responseBody,
                            methodParameter,
                            MediaType.APPLICATION_JSON,
                            JsonbHttpMessageConverter.class,
                            new ServletServerHttpRequest(servletRequest),
                            mock(ServerHttpResponse.class)
                    )
            );

            // then - body 반환 확인
            Assertions.assertEquals(responseBody, result);

            // then - audit log 에 전달된 값 확인
            ArgumentCaptor<String> ipCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> activityCaptor = ArgumentCaptor.forClass(String.class);

            @SuppressWarnings("unchecked")  // 테스트 코드 이므로 warning 단순 제거
            ArgumentCaptor<Map<String, Object>> detailCaptor = ArgumentCaptor.forClass(Map.class);

            verify(mockAuditLogService, times(1)).logApiCall(
                    ipCaptor.capture(),
                    activityCaptor.capture(),
                    detailCaptor.capture()
            );

            Assertions.assertEquals(methodType + " " + uri, activityCaptor.getValue());
            Assertions.assertEquals(ip, ipCaptor.getValue());

            Map<String, Object> detail = detailCaptor.getValue();
            Assertions.assertNotNull(detail);
            Assertions.assertEquals(query, detail.get(DETAIL_KEY_QUERY));
            Assertions.assertEquals(requestBody, detail.get(DETAIL_KEY_REQ_BODY));
            Assertions.assertEquals(responseBody, detail.get(DETAIL_KEY_RESP_BODY));
        }
    }
}
