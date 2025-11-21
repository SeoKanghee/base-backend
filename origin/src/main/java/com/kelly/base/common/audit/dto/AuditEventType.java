package com.kelly.base.common.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * audit event type 정의
 *
 * @author 서강희
 */
@AllArgsConstructor
@Getter
public enum AuditEventType {
    SYSTEM_EVENT(0, "system event"),    // audit log 저장이 필요한 시스템 이벤트
    API_CALL(1000, "call API"); // audit log 저장이 필요한 API 호출

    private final Integer code;
    private final String description;
}
