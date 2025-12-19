package com.kelly.base.common.i18n;

import com.kelly.base.common.exception.CommonRuntimeException;
import com.kelly.base.common.interfaces.II18nMessageService;
import com.kelly.base.common.response.CommonResultCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * i18n message service
 *
 * @author 서강희
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class I18nMessageService implements II18nMessageService {

    // bean creation via I18nMessageSourceConfig
    @Qualifier("commonMessageSource")
    private final MessageSource commonMessageSource;

    @Qualifier("regulatorMessageSources")
    private final Map<String, MessageSource> regulatorMessageSources;

    private final I18nProperties i18nProperties;

    private final Map<String, Locale> localeCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        log.info("initializing i18n message - default language: {}, default regulatory: {}",
                 i18nProperties.getDefaultLanguage(), i18nProperties.getDefaultRegulator());
        log.info("available regulator sources: {}", regulatorMessageSources.keySet());
    }

    @Override
    public String getMessage(final String tid) {
        return resolveMessage(tid, null);
    }

    @Override
    public String getMessage(final String tid, final Object... args) {
        return resolveMessage(tid, args);
    }

    @Override
    public synchronized void reload() {
        log.info("reloading i18n message sources...");

        try {
            clearCommonMessageSource();     // 공통 MessageSource 캐시 클리어
            clearRegulatorMessageSource();  // 규제기관별 MessageSource 캐시 클리어
        } catch (Exception e) {
            // 예상 exception : UnsupportedOperationException, NullPointerException
            log.error("exception details", e);
            throw new CommonRuntimeException(CommonResultCode.INTERNAL_SERVER_ERROR, "failed to reload i18n messages");
        }

        log.info("i18n messages reloaded successfully");
    }

    /**
     * message 조회
     *
     * @param tid  token ID
     * @param args 메시지 포맷 파라미터
     * @return 조회된 메시지 ( 없는 경우 token ID )
     */
    String resolveMessage(final String tid, final Object[] args) {
        final String language = I18nContext.getLanguage();
        final String regulator = I18nContext.getRegulator();

        // 1. 현재 언어로 조회 시도 ( 언어를 못찾을 경우 default 인 en 으로 조회 )
        final String message = tryGetMessage(tid, language, regulator, args);
        if (message != null) {
            return message;
        }

        // 2. token ID 반환
        log.trace("message not found: {}, {}, {}", tid, language, regulator);
        return tid;
    }

    /**
     * 언어 + 규제기관으로 메시지 조회 시도
     *
     * @param tid       token ID
     * @param language  언어 코드
     * @param regulator 규제기관 코드
     * @param args      메시지 포맷 파라미터
     * @return 조회된 메시지 ( 조회가 안될 경우 null )
     */
    private String tryGetMessage(final String tid, final String language, final String regulator, final Object[] args) {
        final Locale locale = getLocale(language);

        // 1. 규제기관별 MessageSource 조회
        try {
            return tryGetMessageWithRegulator(tid, locale, regulator, args);
        } catch (Exception e) {
            // 예상 exception : NullPointerException, NoSuchMessageException
            log.trace("message not found in regulator [{}] source: {}", regulator, tid);
        }

        // 2. 공통 MessageSource 조회
        try {
            return commonMessageSource.getMessage(tid, args, locale);
        } catch (NoSuchMessageException e) {
            log.trace("message not found in common source: {}", tid);
        }

        return null;
    }

    private String tryGetMessageWithRegulator(final String tid, final Locale locale, final String regulator,
                                              final Object[] args) {
        final MessageSource regulatorMsgSrc = regulatorMessageSources.get(regulator);

        // 규제기관 properties 파일이 없는 경우 null 이 반환됨
        return Objects.requireNonNull(regulatorMsgSrc).getMessage(tid, args, locale);
    }

    /**
     * Locale 객체 조회
     *
     * @param language 언어 코드
     * @return <code>Locale</code> 객체
     */
    private Locale getLocale(final String language) {
        return localeCache.computeIfAbsent(language, Locale::new);
    }

    /**
     * common message source 정리
     */
    void clearCommonMessageSource() {
        if (commonMessageSource instanceof ReloadableResourceBundleMessageSource reloadableMsgSrc) {
            reloadableMsgSrc.clearCache();
            log.debug("cleared common message source cache");
        }
    }

    /**
     * regulator message source 정리
     */
    void clearRegulatorMessageSource() {
        for (final Map.Entry<String, MessageSource> entry : regulatorMessageSources.entrySet()) {
            if (entry.getValue() instanceof ReloadableResourceBundleMessageSource reloadableMsgSrc) {
                reloadableMsgSrc.clearCache();
                log.debug("cleared regulatory message source cache: {}", entry.getKey());
            }
        }
    }
}
