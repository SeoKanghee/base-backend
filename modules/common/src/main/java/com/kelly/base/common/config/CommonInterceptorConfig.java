package com.kelly.base.common.config;

import com.kelly.base.common.i18n.I18nInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 정의한 interceptor 를 등록
 *
 * @author 서강희
 */
@Configuration
@RequiredArgsConstructor
public class CommonInterceptorConfig implements WebMvcConfigurer {
    private final I18nInterceptor i18nInterceptor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(i18nInterceptor)
                .addPathPatterns("/**");
    }
}
