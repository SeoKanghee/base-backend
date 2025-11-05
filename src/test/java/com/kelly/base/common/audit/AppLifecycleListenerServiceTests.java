package com.kelly.base.common.audit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

@DisplayName("AppLifecycleListenerServiceTests")
class AppLifecycleListenerServiceTests {
    private AuditLogService mockAuditLogService;
    private AppLifecycleListenerService appLifecycleListenerService;

    @BeforeEach
    void init() {
        mockAuditLogService = mock(AuditLogService.class);
        appLifecycleListenerService = new AppLifecycleListenerService(mockAuditLogService);
    }

    @Nested
    @DisplayName("OnApplicationReadyTests")
    class OnApplicationReadyTests {
        @Test
        @DisplayName("onApplicationReady test")
        void onApplicationReadyTest() {
            // when
            Assertions.assertDoesNotThrow(appLifecycleListenerService::onApplicationReady);

            // then - auditLogService.logSystemEventAppStart 가 1번 호출되는지 확인
            verify(mockAuditLogService, times(1)).logSystemEventAppStart();
        }
    }

    @Nested
    @DisplayName("OnApplicationShutdownTests")
    class OnApplicationShutdownTests {
        @Test
        @DisplayName("onApplicationShutdown test")
        void onApplicationShutdownTest() {
            // when
            Assertions.assertDoesNotThrow(appLifecycleListenerService::onApplicationShutdown);

            // then - auditLogService.logSystemEventAppStart 가 1번 호출되는지 확인
            verify(mockAuditLogService, times(1)).logSystemEventAppShutdown();
        }
    }
}
