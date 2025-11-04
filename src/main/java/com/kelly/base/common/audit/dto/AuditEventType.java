package com.kelly.base.common.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuditEventType {
    SYSTEM_EVENT(0, "system event"),
    API_CALL(1000, "call API");

    private final Integer code;
    private final String description;
}
