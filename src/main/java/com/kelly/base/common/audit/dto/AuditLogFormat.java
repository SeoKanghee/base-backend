package com.kelly.base.common.audit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kelly.base.common.CommonConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Generated;

import java.time.ZonedDateTime;
import java.util.Map;

@Generated  // jacoco 제외 - 임시
@Schema(description = "log - audit")
public record AuditLogFormat(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.DateTimePattern.AUDIT_LOG_PATTERN)
        ZonedDateTime creationDateTime,
        AuditEventType eventType,
        String ipAddress,
        String activity,
        Map<String, Object> activityDetail,
        String applicationInfo
) {
    // creationDateTime : 시간 정보
    // eventType : 이벤트 분류 정보
    // ipAddress : 호출 대상 IP
    // activity : 동작 정보
    // activityDetail : 동작 상세 정보 ( payload.. )
    // applicationInfo : 앱 정보
}
