package com.kelly.base.product.identity.response;

import com.kelly.base.common.interfaces.IStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IdentityStatusCode implements IStatusCode {
    // 8(auth) + xxx(http status code) + yyyy(unique code)
    ISC_82000001(82000001, "[OK] password change is required"),
    ISC_84010001(84010001, "[UNAUTHORIZED] invalid userId or password"),
    ISC_84030001(84030001, "[FORBIDDEN] this account has been locked"),
    ISC_84030002(84030002, "[FORBIDDEN] this account is currently disabled");

    private final Integer code;
    private final String message;
}
