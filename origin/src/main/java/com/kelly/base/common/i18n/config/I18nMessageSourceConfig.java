package com.kelly.base.common.i18n.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelly.base.common.i18n.I18nProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * i18n MessageSource 설정
 *
 * @author 서강희
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class I18nMessageSourceConfig {

    private final I18nProperties i18nProperties;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    /**
     * 공통 message source ( json )
     */
    @Bean("commonMessageSource")
    public MessageSource commonMessageSource() {
        JsonReloadableMessageSource messageSource = new JsonReloadableMessageSource(objectMapper);

        final String basename = i18nProperties.getResourcePath() + "/common/messages";
        configureMessageSourceDefaultOptions(messageSource, basename);

        log.info("configured common messageSource with basename: {}", basename);
        return messageSource;
    }

    /**
     * 규제기관별 message source ( properties )
     */
    @Bean("regulatorMessageSources")
    public Map<String, MessageSource> regulatorMessageSources() {
        Map<String, MessageSource> sources = new HashMap<>();

        try {
            // 프로덕트 리소스 파일 스캔
            Map<String, MessageSource> scannedSources = scanAndCreateRegulatorSources();
            sources.putAll(scannedSources);

            log.info("configured {} regulator MessageSources: {}",
                     sources.size(), sources.keySet());
        } catch (Exception e) {
            // 예상 exception : NullPointerException
            log.error("failed to scan regulator messageSources", e);
        }

        return sources;
    }

    /**
     * message source 의 공통 기본 설정 적용
     *
     * @param messageSource message source
     * @param basename      basename
     */
    void configureMessageSourceDefaultOptions(
            final ReloadableResourceBundleMessageSource messageSource, final String basename
    ) {
        messageSource.setBasename(basename);
        messageSource.setDefaultLocale(new Locale(i18nProperties.getDefaultLanguage()));
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());

        // 최초 로딩 후 갱신 하지 않음
        messageSource.setCacheSeconds(-1);

        // locale 정보가 없을 때 system locale 과 상관 없이 항상 default 를 보도록 설정
        messageSource.setFallbackToSystemLocale(false);
    }

    /**
     * product message source 의 파일 이름을 확인해서 규제기관 별 message source 로딩
     */
    private Map<String, MessageSource> scanAndCreateRegulatorSources() throws IOException {
        final Map<String, MessageSource> sources = new HashMap<>();

        if (resourceLoader instanceof ResourcePatternResolver resolver) {
            String locationPattern = i18nProperties.getResourcePath() + "/product/messages_*.properties";
            Resource[] resources = resolver.getResources(locationPattern);

            for (Resource resource : resources) {
                // 규제기관별 properties 파일을 로딩해서 message source map 에 추가
                putRegulatorMessageSource(sources, resource);
            }
        }

        return sources;
    }

    private void putRegulatorMessageSource(final Map<String, MessageSource> sources, final Resource resource) {
        // 파일 패턴: messages_{규제기관}_{언어}.properties
        final Pattern pattern = Pattern.compile("messages_([a-z]+)_([a-z]{2,3})\\.properties$");

        final String filename = resource.getFilename();
        if (filename != null) {
            Matcher matcher = pattern.matcher(filename);
            if (matcher.find()) {
                final String regulator = matcher.group(1);  // 규제기관 이름 추출 (첫 번째 그룹)
                final MessageSource messageSource = createRegulatorMessageSource(regulator);
                sources.putIfAbsent(regulator, messageSource);
            }
        }
    }

    private MessageSource createRegulatorMessageSource(final String regulator) {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        final String basename = i18nProperties.getResourcePath() + "/product/messages_" + regulator;
        configureMessageSourceDefaultOptions(messageSource, basename);

        log.debug("created messageSource for regulator: {} with basename: {}", regulator, basename);
        return messageSource;
    }
}
