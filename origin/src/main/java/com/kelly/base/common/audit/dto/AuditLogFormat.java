package com.kelly.base.common.audit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kelly.base.common.CommonConstants;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * audit log 정의
 *
 * @param creationDateTime 시간 정보 <code>ZonedDateTime</code>
 * @param eventType        이벤트 분류 정보
 * @param ipAddress        호출 대상 IP
 * @param activity         동작 정보
 * @param activityDetail   동작 상세 정보 ( payload.. )
 * @param productVersion   앱 정보
 * @author 서강희
 */
@Schema(description = "log - audit")
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)  // json 처리시 첫글자 대문자로 변경
public record AuditLogFormat(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstants.AuditConstants.LOG_PATTERN)
        ZonedDateTime creationDateTime,
        AuditEventType eventType,
        String ipAddress,
        String activity,
        Map<String, Object> activityDetail,
        String productVersion
) {
}
