package com.kelly.base.common.interfaces;

/**
 * 사용자 정보 확장 인터페이스
 *
 * @author 서강희
 */
public interface IExtUserInfo {
    
    /**
     * 사용자의 언어 코드를 반환합니다.
     * 
     * @return 언어 코드 (예: "ko", "en")
     */
    String getLanguageCode();
    
    /**
     * 순차적으로 할당된 Account ID를 반환합니다.
     * 
     * @return Account ID
     */
    long getAccountId();
}
