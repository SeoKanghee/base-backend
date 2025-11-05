package com.kelly.base.common.audit;

import com.kelly.base.common.audit.dto.AuditEventType;
import com.kelly.base.common.audit.dto.AuditLogFormat;
import com.kelly.base.common.config.CommonPropertiesConfig;
import com.kelly.base.common.utils.DateTimeUtil;
import com.kelly.base.common.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {
    // logback-spring.xml 의 logger name 과 일치
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOGGER");

    private static final String NOT_APPLICABLE = "N/A";

    private final CommonPropertiesConfig commonPropertiesConfig;

    public void logApiCall(final String ipAddress, final String activity, final Map<String, Object> activityDetail) {
        final String productVersion = getProductVersion();
        final AuditLogFormat auditLog = new AuditLogFormat(
                DateTimeUtil.nowUtc(), AuditEventType.API_CALL, ipAddress, activity, activityDetail, productVersion
        );
        logAudit(auditLog);
    }

    public void logSystemEvent(final String activity, final Map<String, Object> activityDetail) {
        final String productVersion = getProductVersion();
        final AuditLogFormat auditLog = new AuditLogFormat(
                DateTimeUtil.nowUtc(), AuditEventType.SYSTEM_EVENT, NOT_APPLICABLE,
                activity, activityDetail, productVersion
        );
        logAudit(auditLog);
    }

    public void logSystemEventAppStart() {
        logSystemEvent("Server Start", Map.of("message", "application started successfully"));
    }

    public void logSystemEventAppShutdown() {
        logSystemEvent("Server Shutdown", Map.of("message", "application shutdown initiated"));
    }

    void logAudit(final AuditLogFormat auditLog) {
        try {
            final String jsonLog = JsonUtil.convert(auditLog, false);
            auditLogger.info(jsonLog);
        } catch (Exception e) {
            // audit log 가 저장되지 못한 상황이므로 error log 출력
            log.error("failed to write audit log - contents : {}", auditLog, e);
        }
    }

    private String getProductVersion() {
        return commonPropertiesConfig.getApplicationName() + " v" + commonPropertiesConfig.getApplicationVersion();
    }
}
