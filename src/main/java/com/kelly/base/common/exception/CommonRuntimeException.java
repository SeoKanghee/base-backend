package com.kelly.base.common.exception;

import com.kelly.base.common.enums.CommonResultCode;
import com.kelly.base.common.interfaces.ICommonException;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.NonNull;

@Getter
@ToString
public class CommonRuntimeException extends RuntimeException implements ICommonException {
    // AOP 에서 반환되는 Exception 은 checked 로 처리되지 않음
    // RuntimeException 의 extends 로 선언 해서 처리
    // - reference : https://www.baeldung.com/java-undeclaredthrowableexception

    private final CommonResultCode resultCode;
    private final Integer extraCode;
    private final String extraMessage;

    public CommonRuntimeException(Exception e) {
        super(e);
        this.resultCode = CommonResultCode.INTERNAL_SERVER_ERROR;
        this.extraCode = null;
        this.extraMessage = null;
    }

    public CommonRuntimeException(@NonNull CommonResultCode resultCode) {
        this(resultCode, null, null);
    }

    public CommonRuntimeException(@NonNull CommonResultCode resultCode, String extraMessage) {
        this(resultCode, null, extraMessage);
    }

    public CommonRuntimeException(@NonNull CommonResultCode resultCode, Integer extraCode, String extraMessage) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.extraCode = extraCode;
        this.extraMessage = extraMessage;
    }
}
