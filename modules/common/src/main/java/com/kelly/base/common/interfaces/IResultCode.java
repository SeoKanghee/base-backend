package com.kelly.base.common.interfaces;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

public interface IResultCode extends Serializable {
    // product 에서 필요한 http status, status code, message 를 정의해서 사용
    HttpStatus getHttpStatus();
    Integer getCode();
    String getMessage();
}
