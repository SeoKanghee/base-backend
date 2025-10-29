package com.kelly.base.common.interfaces;

import com.kelly.base.common.enums.CommonResultCode;

public interface ICommonException {
    // 구현체에서 resultCode, extraCode, extraMessage 를 필드로 정의하고 @Getter 만 추가
    CommonResultCode getResultCode();
    Integer getExtraCode();
    String getExtraMessage();
}
