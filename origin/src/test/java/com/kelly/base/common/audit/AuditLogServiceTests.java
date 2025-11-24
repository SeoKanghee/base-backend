package com.kelly.base.common.audit;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.kelly.base.common.audit.dto.AuditEventType;
import com.kelly.base.common.audit.dto.AuditLogFormat;
import com.kelly.base.common.audit.provider.DefaultAuditContextProvider;
import com.kelly.base.common.config.CommonPropertiesConfig;
import com.kelly.base.common.exception.CommonException;
import com.kelly.base.common.utils.DateTimeUtil;
import com.kelly.base.common.utils.JsonUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@DisplayName("AuditLogServiceTests")
class AuditLogServiceTests {
    private ListAppender<ILoggingEvent> logAppender;
    private Logger auditLogger;

    private AuditLogService auditLogService;

    @BeforeEach
    void init() {
        // mocking CommonPropertiesConfig
        final CommonPropertiesConfig mockPropertiesConfig = mock(CommonPropertiesConfig.class);
        when(mockPropertiesConfig.getApplicationName()).thenReturn("test");
        when(mockPropertiesConfig.getApplicationVersion()).thenReturn("1.0.0");

        // AuditLogService 초기화
        auditLogService = new AuditLogService(mockPropertiesConfig, new DefaultAuditContextProvider());

        // log 수집을 위한 ListAppender 설정
        auditLogger = (Logger) LoggerFactory.getLogger("AUDIT_LOGGER");
        logAppender = new ListAppender<>();
        logAppender.start();
        auditLogger.addAppender(logAppender);
    }

    @AfterEach
    void release() {
        // 테스트 완료후 ListAppender 제거
        if (logAppender != null) {
            logAppender.stop();
            auditLogger.detachAppender(logAppender);
        }
    }

    void commonVerification(final AuditLogFormat auditLog,
                            final AuditEventType eventType, final String ipAddress, final String activity) {
        // 생성된 log 의 공통 검증 항목
        Assertions.assertNotNull(auditLog.creationDateTime());
        Assertions.assertEquals(eventType, auditLog.eventType());
        Assertions.assertEquals(ipAddress, auditLog.ipAddress());
        Assertions.assertEquals(activity, auditLog.activity());
        Assertions.assertEquals("test v1.0.0", auditLog.productVersion());
    }

    @Nested
    @DisplayName("LogAuditTests")
    class LogAuditTests {
        @Test
        @SuppressWarnings("CollectionAddedToSelf")  // 해당 키워드로 warning 처리가 안되면 all 사용
        @DisplayName("logAudit test - exception")
        void logAuditExceptionTest() {
            // given
            final Map<String, Object> circularDetail = new HashMap<>();
            circularDetail.put("error", circularDetail);  // json 파싱에서 오류가 발생하도록 순환 참조 맵 생성

            final AuditLogFormat errorAuditLog = new AuditLogFormat(
                    DateTimeUtil.nowUtc(), AuditEventType.API_CALL, "192.168.1.119",
                    "error", circularDetail, "test v1.0.0", null
            );

            // when, then - exception 이 app 동작에 영향을 미치지 않도록 log 만 출력
            Assertions.assertDoesNotThrow(
                    () -> auditLogService.logAudit(errorAuditLog)
            );
        }
    }

    @Nested
    @DisplayName("LogApiCallTests")
    class LogApiCallTests {
        @Test
        @DisplayName("logApiCall test - request payload exists")
        void logApiCallPayloadExistsTest() throws CommonException {
            // given
            final String ipAddress = "192.168.1.119";
            final String activity = "POST /api/auth/login";
            final Map<String, Object> activityDetail = Map.of(
                    "loginId", "user01",
                    "password", "*****"
            );

            // when
            Assertions.assertDoesNotThrow(
                    () -> auditLogService.logApiCall(ipAddress, activity, activityDetail)
            );

            // then - log 가 생성됐는지 확인
            final List<ILoggingEvent> logList = logAppender.list;
            Assertions.assertEquals(1, logList.size());

            // then - log 내용 검증
            final ILoggingEvent logEvent = logList.get(0);
            Assertions.assertNotNull(logEvent);

            final String logMessage = logEvent.getMessage();
            final AuditLogFormat auditLog = JsonUtil.parse(logMessage, AuditLogFormat.class);
            Assertions.assertNotNull(auditLog);

            commonVerification(auditLog, AuditEventType.API_CALL, ipAddress, activity);
            Assertions.assertEquals("user01", auditLog.activityDetail().get("loginId"));
            Assertions.assertEquals("*****", auditLog.activityDetail().get("password"));
        }

        @Test
        @DisplayName("logApiCall test - request payload is null")
        void logApiCallPayloadNullTest() throws CommonException {
            // given
            final String ipAddress = "192.168.1.119";
            final String activity = "GET /api/user/search";

            // when
            Assertions.assertDoesNotThrow(
                    () -> auditLogService.logApiCall(ipAddress, activity, null)
            );

            // then - log 가 생성됐는지 확인
            final List<ILoggingEvent> logList = logAppender.list;
            Assertions.assertEquals(1, logList.size());

            // then - log 내용 검증
            final ILoggingEvent logEvent = logList.get(0);
            Assertions.assertNotNull(logEvent);

            final String logMessage = logEvent.getMessage();
            final AuditLogFormat auditLog = JsonUtil.parse(logMessage, AuditLogFormat.class);
            Assertions.assertNotNull(auditLog);

            commonVerification(auditLog, AuditEventType.API_CALL, ipAddress, activity);
            Assertions.assertNull(auditLog.activityDetail());
        }

        @Test
        @DisplayName("logApiCall test - request payload is empty")
        void logApiCallPayloadEmptyTest() throws CommonException {
            // given
            final String ipAddress = "192.168.1.119";
            final String activity = "GET /api/user/search";
            final Map<String, Object> activityDetail = Map.of();

            // when
            Assertions.assertDoesNotThrow(
                    () -> auditLogService.logApiCall(ipAddress, activity, activityDetail)
            );

            // then - log 가 생성됐는지 확인
            final List<ILoggingEvent> logList = logAppender.list;
            Assertions.assertEquals(1, logList.size());

            // then - log 내용 검증
            final ILoggingEvent logEvent = logList.get(0);
            Assertions.assertNotNull(logEvent);

            final String logMessage = logEvent.getMessage();
            final AuditLogFormat auditLog = JsonUtil.parse(logMessage, AuditLogFormat.class);
            Assertions.assertNotNull(auditLog);

            commonVerification(auditLog, AuditEventType.API_CALL, ipAddress, activity);
            Assertions.assertTrue(auditLog.activityDetail().isEmpty());
        }
    }

    @Nested
    @DisplayName("LogSystemEventTests")
    class LogSystemEventTests {
        @Test
        @DisplayName("logSystemEvent test")
        void logSystemEventTest() throws CommonException {
            // given
            final String activity = "Run Scheduler";
            final Map<String, Object> activityDetail = Map.of(
                    "message", "scheduler is starting"
            );

            // when
            Assertions.assertDoesNotThrow(
                    () -> auditLogService.logSystemEvent(activity, activityDetail)
            );

            // then - log 가 생성됐는지 확인
            final List<ILoggingEvent> logList = logAppender.list;
            Assertions.assertEquals(1, logList.size());

            // then - log 내용 검증
            final ILoggingEvent logEvent = logList.get(0);
            Assertions.assertNotNull(logEvent);

            final String logMessage = logEvent.getMessage();
            final AuditLogFormat auditLog = JsonUtil.parse(logMessage, AuditLogFormat.class);
            Assertions.assertNotNull(auditLog);

            commonVerification(auditLog, AuditEventType.SYSTEM_EVENT, "N/A", activity);
            Assertions.assertEquals("scheduler is starting", auditLog.activityDetail().get("message"));
        }

        @Test
        @DisplayName("logSystemEventAppStart test")
        void logSystemEventAppStartTest() throws CommonException {
            // when
            Assertions.assertDoesNotThrow(
                    () -> auditLogService.logSystemEventAppStart()
            );

            // then - log 가 생성됐는지 확인
            final List<ILoggingEvent> logList = logAppender.list;
            Assertions.assertEquals(1, logList.size());

            // then - log 내용 검증
            final ILoggingEvent logEvent = logList.get(0);
            Assertions.assertNotNull(logEvent);

            final String logMessage = logEvent.getMessage();
            final AuditLogFormat auditLog = JsonUtil.parse(logMessage, AuditLogFormat.class);
            Assertions.assertNotNull(auditLog);

            commonVerification(auditLog, AuditEventType.SYSTEM_EVENT, "N/A", "Server Start");
            Assertions.assertEquals("application started successfully", auditLog.activityDetail().get("message"));
        }

        @Test
        @DisplayName("logSystemEventAppShutdown test")
        void logSystemEventAppShutdownTest() throws CommonException {
            // when
            Assertions.assertDoesNotThrow(
                    () -> auditLogService.logSystemEventAppShutdown()
            );

            // then - log 가 생성됐는지 확인
            final List<ILoggingEvent> logList = logAppender.list;
            Assertions.assertEquals(1, logList.size());

            // then - log 내용 검증
            final ILoggingEvent logEvent = logList.get(0);
            Assertions.assertNotNull(logEvent);

            final String logMessage = logEvent.getMessage();
            final AuditLogFormat auditLog = JsonUtil.parse(logMessage, AuditLogFormat.class);
            Assertions.assertNotNull(auditLog);

            commonVerification(auditLog, AuditEventType.SYSTEM_EVENT, "N/A", "Server Shutdown");
            Assertions.assertEquals("application shutdown initiated", auditLog.activityDetail().get("message"));
        }
    }
}
