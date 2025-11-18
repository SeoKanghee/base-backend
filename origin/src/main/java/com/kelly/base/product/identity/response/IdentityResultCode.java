package com.kelly.base.product.identity.response;

import com.kelly.base.common.interfaces.IResultCode;
import com.kelly.base.common.interfaces.IStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum IdentityResultCode implements IResultCode {
    // ok - 200
    NEED_CHANGE_PASSWORD(HttpStatus.OK, IdentityStatusCode.ISC_82000001),

    // unauthorized - 401
    BAD_CREDENTIAL(HttpStatus.UNAUTHORIZED, IdentityStatusCode.ISC_84010001),
    ALREADY_LOGIN(HttpStatus.UNAUTHORIZED, IdentityStatusCode.ISC_84010002),

    // forbidden - 403
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, IdentityStatusCode.ISC_84030001),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, IdentityStatusCode.ISC_84030002);

    IdentityResultCode(final HttpStatus httpStatus, final IStatusCode statusCode) {
        this.httpStatus = httpStatus;
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
    }

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;
}
