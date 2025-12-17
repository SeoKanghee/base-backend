package com.kelly.base.common.audit;

import com.kelly.base.common.interfaces.IAuditContextProvider;
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

/**
 * audit log 저장을 처리하는 서비스
 *
 * @author 서강희
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogService {
    // logback-spring.xml 의 logger name 과 일치
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOGGER");

    private static final String NOT_APPLICABLE = "N/A";

    private final CommonPropertiesConfig commonPropertiesConfig;

    private final IAuditContextProvider auditContextProvider;

    /**
     * API 호출에 대한 audit log 저장
     *
     * @param ipAddress      호출한 클라이언트의 IP address
     * @param activity       호출된 API 의 method + uri
     * @param activityDetail API 호출시 전달된 request body
     */
    public void logApiCall(final String ipAddress, final String activity, final Map<String, Object> activityDetail) {
        final String productVersion = getProductVersion();
        final String detailedInfo = auditContextProvider.getDetailedInfo();
        final AuditLogFormat auditLog = new AuditLogFormat(
                DateTimeUtil.nowUtc(), AuditEventType.API_CALL, ipAddress, activity, activityDetail, productVersion,
                detailedInfo
        );
        logAudit(auditLog);
    }

    /**
     * 시스템 이벤트 발생에 따른 audit log 저장
     *
     * @param activity       발생한 이벤트
     * @param activityDetail 발생한 이벤트의 추가 정보
     */
    public void logSystemEvent(final String activity, final Map<String, Object> activityDetail) {
        final String productVersion = getProductVersion();
        final AuditLogFormat auditLog = new AuditLogFormat(
                DateTimeUtil.nowUtc(), AuditEventType.SYSTEM_EVENT, NOT_APPLICABLE,
                activity, activityDetail, productVersion, null
        );
        logAudit(auditLog);
    }

    /**
     * audit log 저장 요청 - 어플리케이션 시작
     */
    public void logSystemEventAppStart() {
        logSystemEvent("Server Start", Map.of("message", "application started successfully"));
    }

    /**
     * audit log 저장 요청 - 어플리케이션 종료
     */
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
