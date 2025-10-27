package com.kelly.base.common.dto;

import com.kelly.base.common.enums.CommonResultCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.NonNull;

@Getter
@Setter
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

    public CommonResponse(final int code, @NonNull final String message) {
        this.code = code;
        this.message = message;
    }

    public CommonResponse(@NonNull CommonResultCode webRespCode, final T result) {
        this(webRespCode);
        this.result = result;
    }
}
