package com.kelly.base.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InternalStatusCode {
    ISC_94000001(94000001, "error - invalid parameter"),
    ISC_95030001(95030001, "error - thread usage is not possible");

    private final Integer code;
    private final String message;
}
