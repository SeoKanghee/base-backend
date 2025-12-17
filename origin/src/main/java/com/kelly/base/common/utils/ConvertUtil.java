package com.kelly.base.common.utils;

import com.kelly.base.common.exception.CommonException;
import com.kelly.base.common.response.CommonResultCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * ConvertUtil
 *
 * @author 서강희
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConvertUtil {
    /**
     * String 을 long(primitive type) 으로 변환
     *
     * @param source 변환할 원본 문자열
     * @return 변환된 값 (long)
     * @throws CommonException 변환이 불가능할 경우
     */
    public static long convertStr2Long(final String source) throws CommonException {
        try {
            return Long.parseLong(source);
        } catch (Exception e) {
            // 예상 Exception : NumberFormatException
            throw new CommonException(CommonResultCode.SYSTEM_ERROR, getErrorMessage(e));
        }
    }

    /**
     * String 을 소문자로 변환
     *
     * @param source 변환할 원본 문자열
     * @return 소문자로 변환된 값 ( null 인 경우 동일하게 null 반환 )
     */
    public static String toLowerCase(final String source) {
        return Optional.ofNullable(source).map(String::toLowerCase).orElse(null);
    }

    private static String getErrorMessage(final Exception e) {
        return "[" + e.getClass().getSimpleName() + "] " + e.getMessage();
    }
}
