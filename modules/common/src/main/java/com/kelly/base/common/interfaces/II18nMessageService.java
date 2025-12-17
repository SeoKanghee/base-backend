package com.kelly.base.common.interfaces;

/**
 * i18n message service interface
 *
 * @author 서강희
 */
public interface II18nMessageService {
    /**
     * 기본적인 message 조회
     *
     * @param key 메시지 키 (예: "TID_00001", "EXT_TID_00001")
     * @return 메시지 텍스트
     */
    String getMessage(String key);

    /**
     * MessageFormat 을 사용하여 message 조회
     *
     * @param key  메시지 키
     * @param args 메시지 포맷 파라미터 (예: "{0}님, 환영합니다" -> args[0] = "김절반")
     * @return 포맷팅된 메시지 텍스트
     */
    String getMessage(String key, Object... args);

    /**
     * message resource reload
     * <p>
     * 외부 resource file 변경 후 app 재시작 없이 message 갱신하는 경우 호출
     */
    void reload();
}
