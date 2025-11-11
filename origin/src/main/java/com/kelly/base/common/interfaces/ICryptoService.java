package com.kelly.base.common.interfaces;

import org.springframework.lang.NonNull;

public interface ICryptoService<T> {
    void setCryptoKey(String key);
    String encrypt(@NonNull final T source);
    T decrypt(@NonNull final String source);
}
