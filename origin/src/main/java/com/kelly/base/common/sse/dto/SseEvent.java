package com.kelly.base.common.sse.dto;

import java.time.ZonedDateTime;
import java.util.Map;

public record SseEvent(
        SseEventType eventType,
        String eventId,
        ZonedDateTime timestamp,
        Map<String, String> contents
) {
    // eventType: 이벤트 유형
    // eventId: 이벤트 식별자
    // timestamp: 이벤트가 발생한 시간
    // contents: 이벤트와 관련된 상세 데이터
}

