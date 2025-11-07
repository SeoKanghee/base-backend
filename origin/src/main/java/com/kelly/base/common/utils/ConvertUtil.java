package com.kelly.base.common.utils;

import com.kelly.base.common.exception.CommonException;
import com.kelly.base.common.response.CommonResultCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConvertUtil {
    public static long convertStr2Long(final String source) throws CommonException {
        try {
            return Long.parseLong(source);
        } catch (Exception e) {
            // 예상 Exception : NumberFormatException
            throw new CommonException(CommonResultCode.SYSTEM_ERROR, getErrorMessage(e));
        }
    }

    private static String getErrorMessage(final Exception e) {
        return "[" + e.getClass().getSimpleName() + "] " + e.getMessage();
    }
}
