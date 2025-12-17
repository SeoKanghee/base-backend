package com.kelly.base.common.utils;

import io.viascom.nanoid.NanoId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValueGenerator {
    public static String getNanoId() {
        return NanoId.generate();
    }
}
