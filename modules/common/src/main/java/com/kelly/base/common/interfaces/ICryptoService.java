package com.kelly.base.common.interfaces;

import org.jspecify.annotations.NonNull;

public interface ICryptoService<T> {
    void setCryptoKey(String key);
    String encrypt(final T source);
    T decrypt(@NonNull final String source);
}
