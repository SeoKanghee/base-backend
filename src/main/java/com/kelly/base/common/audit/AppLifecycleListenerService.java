package com.kelly.base.common.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppLifecycleListenerService {
    // audit log 저장을 위한 용도외에는 사용되지 않음
    private final AuditLogService auditLogService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // app 시작시 audit log 저장
        auditLogService.logSystemEventAppStart();
    }

    @EventListener(ContextClosedEvent.class)
    public void onApplicationShutdown() {
        // app 종료시 audit log 저장
        auditLogService.logSystemEventAppShutdown();
    }
}
