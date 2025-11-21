package com.kelly.base.common.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * audit log 에서 제외시킬 controller 에 사용하는 annotation
 *
 * @author 서강희
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoAudit {
    String reason() default "audit is not required";    // audit 제외 사유 명시
}
