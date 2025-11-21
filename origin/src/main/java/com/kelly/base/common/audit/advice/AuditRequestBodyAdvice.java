package com.kelly.base.common.audit.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

import static com.kelly.base.common.CommonConstants.AuditConstants.ATTR_AUDIT_REQ_BODY;

/**
 * audit 처리를 위해 request 에 body 를 attribute 에 저장하는 ControllerAdvice
 *
 * @author 서강희
 */
@Component
@ControllerAdvice
public class AuditRequestBodyAdvice extends RequestBodyAdviceAdapter {
    // https://github.com/spring-projects/spring-framework/blob/main/spring-webmvc/src/main/java/org/springframework/web/servlet/mvc/method/annotation/package-info.java
    // 패키지 내의 모든 method 에 @NullMarked 가 적용되어 해당 parameter 및 return 에 @NonNull 이 적용되어 있음

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Type targetType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;    // request 에서는 default 로 attribute 저장 처리 후 response 에서 @NoAudit 처리
    }

    @NonNull
    @Override
    public Object afterBodyRead(@NonNull Object body, @NonNull HttpInputMessage inputMessage,
                                @NonNull MethodParameter parameter, @NonNull Type targetType,
                                @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        HttpServletRequest req
                = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        req.setAttribute(ATTR_AUDIT_REQ_BODY, body);    // attribute 에 body 저장

        return body;
    }
}
