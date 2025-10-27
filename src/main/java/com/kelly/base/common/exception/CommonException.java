package com.kelly.base.common.exception;

import com.kelly.base.common.enums.CommonResultCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.NonNull;

@Getter
@ToString
public class CommonException extends Exception {
    private final CommonResultCode resultCode;
    private final Integer extraCode;
    private final String extraMessage;

    public CommonException(Exception e) {
        super(e);
        this.resultCode = CommonResultCode.INTERNAL_SERVER_ERROR;
        this.extraCode = null;
        this.extraMessage = null;
    }

    public CommonException(@NonNull CommonResultCode resultCode) {
        this(resultCode, null, null);
    }

    public CommonException(@NonNull CommonResultCode resultCode, String extraMessage) {
        this(resultCode, null, extraMessage);
    }

    public CommonException(@NonNull CommonResultCode resultCode, Integer extraCode, String extraMessage) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.extraCode = extraCode;
        this.extraMessage = extraMessage;
    }
}
