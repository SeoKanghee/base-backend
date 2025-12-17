package com.kelly.base.common.i18n;

import com.kelly.base.common.interfaces.IExtUserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * i18n Interceptor
 * SecurityContext에서 인증된 사용자의 언어 정보를 추출하여 I18nContext에 설정
 *
 * @author 서강희
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class I18nInterceptor implements HandlerInterceptor {
    private final I18nProperties i18nProperties;

    @Override
    public boolean preHandle(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response,
                             @NonNull final Object handler) {
        final String language = getLanguageCode();
        final String regulator = i18nProperties.getDefaultRegulator();

        // 획득한 language 및 regulatory code 를 context 에 업데이트
        I18nContext.setLanguage(language);
        I18nContext.setRegulator(regulator);

        log.debug("I18nContext set - language: {}, regulator: {}", language, regulator);

        return true;
    }

    String getLanguageCode() {
        // SecurityContext에서 인증 정보 가져오기
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String languageCode = i18nProperties.getDefaultLanguage();

        // 로그인한 사용자의 경우 IExtUserInfo 통해 languageCode 사용
        if (authentication != null && authentication.getPrincipal() instanceof IExtUserInfo extUserInfo) {
            languageCode = extUserInfo.getLanguageCode();
            log.debug("user language code from SecurityContext: {} (userId: {})",
                      languageCode, extUserInfo.getAccountId());
        } else {
            log.debug("using default language code: {} (not authenticated)", languageCode);
        }

        return languageCode;
    }

    @Override
    public void afterCompletion(@NonNull final HttpServletRequest request, @NonNull final HttpServletResponse response,
                                @NonNull final Object handler, final Exception ex) {
        I18nContext.clear();    // context 정리
        log.debug("I18nContext cleared");
    }
}
