package com.kelly.base.common.interfaces;

public interface ICommonException {
    // 구현체에서 resultCode, extraCode, extraMessage 를 필드로 정의하고 @Getter 만 추가
    IResultCode getResultCode();
    Integer getExtraCode();
    String getExtraMessage();
}
