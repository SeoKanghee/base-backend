package com.kelly.base.common.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelly.base.common.audit.dto.AuditEventType;
import com.kelly.base.common.audit.dto.AuditLogFormat;
import com.kelly.base.common.config.CommonPropertiesConfig;
import com.kelly.base.common.utils.DateTimeUtil;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Generated  // jacoco 회피 - 임시
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOGGER");

    private static final String NOT_APPLICABLE = "N/A";

    private final ObjectMapper objectMapper;
    private final CommonPropertiesConfig commonPropertiesConfig;

    public void logApiCall(final String ipAddress, final String activity, final Map<String, Object> activityDetail) {
        final String applicationInfo = getApplicationInfo();
        final AuditLogFormat auditLog = new AuditLogFormat(
                DateTimeUtil.nowUtc(), AuditEventType.API_CALL, ipAddress, activity, activityDetail, applicationInfo
        );
        logAudit(auditLog);
    }

    public void logSystemEventAppStart() {
        final String applicationInfo = getApplicationInfo();
        final AuditLogFormat auditLog = new AuditLogFormat(
                DateTimeUtil.nowUtc(), AuditEventType.SYSTEM_EVENT, NOT_APPLICABLE, "Server Start",
                Map.of("message", "application started successfully"), applicationInfo
        );
        logAudit(auditLog);
    }

    public void logSystemEventAppShutdown() {
        final String applicationInfo = getApplicationInfo();
        final AuditLogFormat auditLog = new AuditLogFormat(
                DateTimeUtil.nowUtc(), AuditEventType.SYSTEM_EVENT, NOT_APPLICABLE, "Server Shutdown",
                Map.of("message", "application shutdown initiated"), applicationInfo
        );
        logAudit(auditLog);
    }

    private void logAudit(final AuditLogFormat auditLog) {
        try {
            String jsonLog = objectMapper.writeValueAsString(auditLog);
            auditLogger.info(jsonLog);
        } catch (Exception e) {
            log.error("failed to write audit log", e);
        }
    }

    private String getApplicationInfo() {
        return commonPropertiesConfig.getApplicationName() + " v" + commonPropertiesConfig.getApplicationVersion();
    }
}
