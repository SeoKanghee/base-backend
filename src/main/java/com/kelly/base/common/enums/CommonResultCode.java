package com.kelly.base.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonResultCode {
    // normal
    SUCCESS(HttpStatus.OK),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),

    // specific - 4xx
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, InternalStatusCode.ISC_94000001),

    // specific - 5xx
    TASK_REJECTED(HttpStatus.SERVICE_UNAVAILABLE, InternalStatusCode.ISC_95030001);


    CommonResultCode(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        this.code = httpStatus.value();
        this.message = httpStatus.getReasonPhrase();
    }

    CommonResultCode(final HttpStatus httpStatus, final InternalStatusCode internalStatusCode) {
        this.httpStatus = httpStatus;
        this.code = internalStatusCode.getCode();
        this.message = internalStatusCode.getMessage();
    }

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;
}
