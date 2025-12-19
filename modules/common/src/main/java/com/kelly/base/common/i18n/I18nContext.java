package com.kelly.base.common.i18n;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * i18n Context
 * <p>
 * ThreadLocal을 사용하여 현재 req/resp 의 언어 및 규제기관 정보를 thread 별로 관리
 *
 * @author 서강희
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class I18nContext {
    private static final ThreadLocal<String> language = new ThreadLocal<>();
    private static final ThreadLocal<String> regulator = new ThreadLocal<>();

    /**
     * 현재 thread 의 언어 코드 설정
     *
     * @param langCode 언어 코드 (예: "en", "ko")
     */
    public static void setLanguage(final String langCode) {
        language.set(langCode);
    }

    /**
     * 현재 thread 의 언어 코드 조회
     *
     * @return 언어 코드
     */
    public static String getLanguage() {
        return language.get();
    }

    /**
     * 현재 thread 의 규제기관 코드 설정
     *
     * @param regCode 규제기관 코드 (예: "fda", "ce")
     */
    public static void setRegulator(final String regCode) {
        regulator.set(regCode);
    }

    /**
     * 현재 thread 의 규제기관 코드 조회
     *
     * @return 규제기관 코드
     */
    public static String getRegulator() {
        return regulator.get();
    }

    /**
     * context 정리 -> memory leak 방지
     */
    public static void clear() {
        language.remove();
        regulator.remove();
    }
}
