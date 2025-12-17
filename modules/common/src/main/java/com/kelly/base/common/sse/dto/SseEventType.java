package com.kelly.base.common.sse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SseEventType {
    CONNECT("connect SSE"),
    REFRESH_ANALYSIS("refresh analysis"),
    REFRESH_LICENSE("refresh license"),
    DISCONNECT("disconnect SSE");

    private final String description;
}
