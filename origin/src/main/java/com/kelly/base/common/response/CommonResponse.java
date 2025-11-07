package com.kelly.base.common.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.NonNull;

@Getter
@NoArgsConstructor
@ToString
public class CommonResponse<T> {
    private int code;
    private String message;
    private T result;

    public CommonResponse(@NonNull CommonResultCode resultCode) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
    }

    public CommonResponse(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public CommonResponse(@NonNull CommonResultCode resultCode, final T result) {
        this(resultCode);
        this.result = result;
    }
}
