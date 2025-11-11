package com.kelly.base.common.audit.advice;

import com.kelly.base.common.audit.AuditLogService;
import com.kelly.base.common.audit.annotation.NoAudit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.kelly.base.common.CommonConstants.AuditConstants.*;

@Component
@ControllerAdvice
@RequiredArgsConstructor
public class AuditResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    // https://github.com/spring-projects/spring-framework/blob/main/spring-webmvc/src/main/java/org/springframework/web/servlet/mvc/method/annotation/package-info.java
    // 패키지 내의 모든 method 에 @NullMarked 가 적용되어 해당 parameter 및 return 에 @NonNull 이 적용되어 있음

    private final AuditLogService auditLogService;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    static final String DETAIL_KEY_QUERY = "query";
    static final String DETAIL_KEY_REQ_BODY = "requestPayload";
    static final String DETAIL_KEY_RESP_BODY = "responsePayload";

    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        // @NoAudit 이 적용되어 있는 경우에는 AuditResponseBodyAdvice 처리 제외
        return (!returnType.hasMethodAnnotation(NoAudit.class)) &&  // method
                (!returnType.getDeclaringClass().isAnnotationPresent(NoAudit.class));   // class
    }

    @Override
    public Object beforeBodyWrite(@Nullable Object body,
                                  @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        final HttpServletRequest httpReq = ((ServletServerHttpRequest) request).getServletRequest();
        final String uri = httpReq.getRequestURI();

        if (isAuditExcluded(uri)) {
            return body;
        }

        final String method = httpReq.getMethod();
        final String activity = method + " " + uri;
        final String ip = httpReq.getRemoteAddr();

        final Map<String, Object> detail = new LinkedHashMap<>();
        updateQuery(detail, httpReq.getQueryString());  // query
        updateRequest(detail, httpReq.getAttribute(ATTR_AUDIT_REQ_BODY));   // request payload
        updateResponse(detail, body);   // response payload

        auditLogService.logApiCall(ip, activity, detail);
        return body;
    }

    private boolean isAuditExcluded(final String uri) {
        return EXCLUDED_URI_PATTERNS.stream().anyMatch(pattern -> PATH_MATCHER.match(pattern, uri));
    }

    private void updateQuery(final Map<String, Object> detail, final String queryString) {
        if (queryString != null) {
            detail.put(DETAIL_KEY_QUERY, queryString);
        }
    }

    private void updateRequest(final Map<String, Object> detail, final Object requestBody) {
        if (requestBody != null) {
            detail.put(DETAIL_KEY_REQ_BODY, requestBody);
        }
    }

    private void updateResponse(final Map<String, Object> detail, final Object responseBody) {
        if (responseBody != null) {
            detail.put(DETAIL_KEY_RESP_BODY, responseBody);
        }
    }
}
