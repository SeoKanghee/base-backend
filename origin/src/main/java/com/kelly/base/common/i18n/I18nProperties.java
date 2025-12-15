package com.kelly.base.common.i18n;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * i18n 설정 프로퍼티
 * application.yml의 i18n.* 설정을 바인딩
 *
 * @author 서강희
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "i18n")
public class I18nProperties {

    /**
     * 기본 언어 코드 (비로그인 사용자)
     */
    private String defaultLanguage = "en";

    /**
     * 시스템 규제기관 코드 (비활성화된 시스템)
     */
    private String defaultRegulator = "mfds";

    /**
     * 리소스 파일 기본 경로
     */
    private String resourcePath = "file:./config/i18n";

    /**
     * 캐시 활성화 여부
     */
    private boolean cacheEnabled = true;
}
