package com.kelly.base.common.response;

import com.kelly.base.common.interfaces.IStatusCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SpecificStatusCode implements IStatusCode {
    // 9(error) + xxx(http status code) + yyyy(unique code)
    SSC_94000001(94000001, "[ERROR] invalid parameter"),
    SSC_95000001(95000001, "[ERROR] system error"),
    SSC_95030001(95030001, "[ERROR] thread usage is not possible");

    private final Integer code;
    private final String message;
}
