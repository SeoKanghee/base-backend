package com.kelly.base.common.interfaces;

/**
 * audit log 기록을 위한 컨텍스트 정보를 제공하는 인터페이스<p>
 * 이 인터페이스는 common 패키지에 정의되며, product 패키지에서 구현체를 제공합니다.
 *
 * @author 서강희
 */
public interface IAuditContextProvider {
    /**
     * audit 에 추가할 정보
     * <p>
     * 구현체에서 audit 에 필요한 정보 문자열 형태로 반환합니다.
     *
     * @return 상세 정보 (nullable)
     */
    String getDetailedInfo();
}
