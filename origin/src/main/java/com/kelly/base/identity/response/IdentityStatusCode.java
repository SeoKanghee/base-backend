package com.kelly.base.identity.response;

import com.kelly.base.common.interfaces.IStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * identity 관련 status code 선언
 *
 * @author 서강희
 */
@AllArgsConstructor
@Getter
public enum IdentityStatusCode implements IStatusCode {
    // 8(auth) + xxx(http status code) + yyyy(unique code)
    ISC_82000001(82000001, "[OK] password change is required"),
    ISC_84010001(84010001, "[UNAUTHORIZED] invalid userId or password"),
    ISC_84010002(84010002, "[UNAUTHORIZED] already logged in"),
    ISC_84010003(84010003, "[UNAUTHORIZED] authentication required"),
    ISC_84030001(84030001, "[FORBIDDEN] this account has been locked"),
    ISC_84030002(84030002, "[FORBIDDEN] this account is currently disabled"),
    ISC_84030003(84030003, "[FORBIDDEN] access denied");

    private final Integer code;
    private final String message;
}
