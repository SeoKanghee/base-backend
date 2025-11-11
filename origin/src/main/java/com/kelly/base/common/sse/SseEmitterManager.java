package com.kelly.base.common.sse;

import com.kelly.base.common.config.CommonPropertiesConfig;
import com.kelly.base.common.sse.dto.SseEvent;
import com.kelly.base.common.sse.dto.SseEventType;
import com.kelly.base.common.utils.DateTimeUtil;
import com.kelly.base.common.utils.ValueGenerator;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseEmitterManager {
    // https://developer.mozilla.org/ko/docs/Web/API/Server-sent_events/Using_server-sent_events

    private final CommonPropertiesConfig commonPropertiesConfig;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    private long sseTimeout = 60000L;   // 60초 (기본값)

    @PostConstruct
    void init() {
        if (commonPropertiesConfig.getSseEmitterTimeoutMs() != null) {
            sseTimeout = commonPropertiesConfig.getSseEmitterTimeoutMs();
            log.info("update SSE timeout (ms) : 60000 -> {}", sseTimeout);
        }
    }

    /**
     * SseEmitter 생성 및 등록
     *
     * @param userId user ID
     * @return 등록된 SseEmitter
     */
    public SseEmitter createEmitter(final long userId) {
        final SseEmitter emitter = registerEmitter(userId);

        emitter.onTimeout(() -> {
            log.error("SSE connection timeout - userId: {}", userId);
            emitter.complete();
            removeEmitter(userId);
        });

        emitter.onError(throwable -> {
            log.error("SSE connection error - userId: {}, error: {}", userId, throwable.getMessage());
            removeEmitter(userId);
        });

        emitter.onCompletion(() -> {
            log.info("SSE connection completion - userId: {}", userId);
            removeEmitter(userId);
        });

        return emitter;
    }

    /**
     * 특정 사용자를 타겟으로 SSE 전송
     *
     * @param userId user ID
     * @param event  전송할 event
     * @return 전송 성공 여부
     */
    public boolean sendToUser(final long userId, final SseEvent event) {
        final SseEmitter emitter = emitters.get(userId);

        if (emitter == null) {
            log.debug("SseEmitter not found - userId: {}", userId);
            return false;
        }

        try {
            emitter.send(
                    SseEmitter.event().name(event.eventType().name()).id(event.eventId()).data(event.contents())
            );
            log.debug("transmission successful - userId : {}, eventType : {}", userId, event.eventType());
            return true;
        } catch (Exception e) {
            // 예상 Exception : IOException, IllegalStateException
            log.error("transmission failure - userId : {}, error : {}", userId, e.getMessage());
            removeEmitter(userId);
            return false;
        }
    }

    /**
     * 모든 사용자에게 SSE 전송
     *
     * @param event 전송할 이벤트
     */
    public void sendToAll(final SseEvent event) {
        log.info("SSE broadcast - eventType : {}, target count : {}", event.eventType(), emitters.size());
        emitters.keySet().forEach(userId -> sendToUser(userId, event));
    }

    /**
     * Connect Event 생성
     *
     * @param userId user ID
     * @return 생성된 SseEvent
     */
    public SseEvent createConnectEvent(final long userId) {
        return createControlEvent(userId, SseEventType.CONNECT);
    }

    /**
     * Disconnect Event 생성
     *
     * @param userId user ID
     * @return 생성된 SseEvent
     */
    public SseEvent createDisconnectEvent(final long userId) {
        return createControlEvent(userId, SseEventType.DISCONNECT);
    }

    /**
     * 현재 연결된 사용자 수
     *
     * @return 연결된 사용자 수
     */
    public int getConnectionCount() {
        return emitters.size();
    }

    /**
     * 특정 사용자의 연결 여부 확인
     *
     * @param userId user ID
     * @return 연결 여부
     */
    public boolean isConnected(final long userId) {
        return emitters.containsKey(userId);
    }

    /**
     * DISCONNECT 이벤트를 전송하고 연결 종료
     *
     * @param userId 사용자 ID
     */
    public void disconnect(final long userId, final SseEvent disconnectEvent) {
        sendToUser(userId, disconnectEvent);    // disconnect event 전송
        removeEmitter(userId);
    }

    // SseEmitter 의 기존 연결을 해제하고 신규로 등록
    private SseEmitter registerEmitter(final long userId) {
        // 1. 기존 연결 제거
        if (emitters.containsKey(userId)) {
            removeEmitter(userId);
        }

        // 2. emitter 생성 및 등록
        final SseEmitter emitter = new SseEmitter(sseTimeout);
        emitters.put(userId, emitter);
        log.info("creating SSE connection - userId : {}, connections : {}", userId, emitters.size());

        return emitter;
    }

    // SseEmitter 를 map 에서 제거
    private void removeEmitter(final long userId) {
        final SseEmitter emitter = emitters.remove(userId);
        if (emitter != null) {
            try {
                emitter.complete();
            } catch (Exception e) {
                log.debug("error during emitter completion processing (ignorable) - userId: {}", userId);
            }
            log.info("remove SSE connection - userId : {}, connections : {}", userId, emitters.size());
        }
    }

    // 제어 ( 연결, 해제 ) SseEvent 생성
    private SseEvent createControlEvent(final long userId, final SseEventType sseEventType) {
        return new SseEvent(
                sseEventType,
                ValueGenerator.getNanoId(),
                DateTimeUtil.nowUtc(),
                Map.of("userId", String.valueOf(userId))
        );
    }
}
