package com.kelly.base.common.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Generated  // jacoco 제외 - 임시
@AllArgsConstructor
@Getter
public enum AuditEventType {
    SYSTEM_EVENT(0, "system event"),
    API_CALL(1000, "call API");

    private final Integer code;
    private final String description;
}
