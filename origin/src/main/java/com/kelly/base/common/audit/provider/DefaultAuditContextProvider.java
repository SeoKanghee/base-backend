package com.kelly.base.common.audit.provider;

/**
 * AuditContextProvider의 기본 구현체
 * <p>
 * product 패키지에서 구체적인 구현체를 제공하지 않을 경우 사용되는 fallback 구현입니다.
 *
 * @author 서강희
 */
public class DefaultAuditContextProvider implements AuditContextProvider {
    @Override
    public String getDetailedInfo() {
        return null;
    }
}
