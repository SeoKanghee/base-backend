package com.kelly.base.common.response;

import com.kelly.base.common.interfaces.IResultCode;
import com.kelly.base.common.interfaces.IStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonResultCode implements IResultCode {
    // normal
    SUCCESS(HttpStatus.OK),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),

    // specific - 4xx
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, CommonStatusCode.CSC_94000001),

    // specific - 5xx
    SYSTEM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, CommonStatusCode.CSC_95000001),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, CommonStatusCode.CSC_95000002),
    TASK_REJECTED(HttpStatus.SERVICE_UNAVAILABLE, CommonStatusCode.CSC_95030001);


    CommonResultCode(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        this.code = httpStatus.value();
        this.message = httpStatus.getReasonPhrase();
    }

    CommonResultCode(final HttpStatus httpStatus, final IStatusCode statusCode) {
        this.httpStatus = httpStatus;
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
    }

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;
}
