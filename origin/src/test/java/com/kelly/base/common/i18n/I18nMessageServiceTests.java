package com.kelly.base.common.i18n;

import com.kelly.base.common.config.CommonBeanConfig;
import com.kelly.base.common.exception.CommonRuntimeException;
import com.kelly.base.common.i18n.config.I18nMessageSourceConfig;
import com.kelly.base.common.response.CommonResultCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = I18nProperties.class)
@Import({ CommonBeanConfig.class, I18nMessageSourceConfig.class, I18nMessageService.class })
@TestPropertySource(properties = {
        "i18n.resource-path=classpath:config/i18n"  // 테스트용 리소스 경로
})
@DisplayName("I18nMessageServiceTests")
class I18nMessageServiceTests {
    @Autowired
    private I18nMessageService i18nMessageService;

    @Nested
    @DisplayName("getMessageTests")
    class GetMessageTests {
        @ParameterizedTest
        @CsvSource({
                // common
                "en, mfds, TID_99991, Hello",
                "ko, mfds, TID_99991, 안녕",
                "ae, mfds, TID_99991, Hello",   // 아베스타어 미지원 -> 기본값(영어)
                "en, fda, TID_99991, Hello",
                "ko, fda, TID_99991, 안녕",
                "ae, fda, TID_99991, Hello",    // 아베스타어 미지원 -> 기본값(영어)
                // product
                "en, mfds, EXT_TID_90001, Hello there!",
                "ko, mfds, EXT_TID_90001, 안녕하세요!",
                "ae, mfds, EXT_TID_90001, Hello there!",    // 아베스타어 미지원 -> 기본값(영어)
                "en, fda, EXT_TID_90001, Hello there!",
                "ko, fda, EXT_TID_90001, 안녕하신가!",
                "ae, fda, EXT_TID_90001, Hello there!",     // 아베스타어 미지원 -> 기본값(영어)
                // 없는 tid
                "en, mfds, UNKNOWN, UNKNOWN",
        })
        @DisplayName("getMessage test - 일반적인 message 확인")
        void getMessageCommonTest(final String languageCode, final String regulator, final String tid,
                                  final String expectedResult) {
            // given - i18nContext 설정
            I18nContext.setLanguage(languageCode);
            I18nContext.setRegulator(regulator);

            // when
            final String result = Assertions.assertDoesNotThrow(
                    () -> i18nMessageService.getMessage(tid)
            );

            // then
            Assertions.assertEquals(expectedResult, result);
        }

        @ParameterizedTest
        @CsvSource({
                // common
                "en, mfds, TID_99992, 왈도, , 'Hello, 왈도'",
                "en, fda, TID_99992, Waldo, , 'Hello, Waldo'",
                "ko, mfds, TID_99992, 왈도, , '왈도, 안녕'",
                "ko, fda, TID_99992, Waldo, , 'Waldo, 안녕'",
                // product
                "en, fda, EXT_TID_90004, <1>, <2>, '<1> Power Lich / <2> Giant Lich'",
                "ko, fda, EXT_TID_90004, <하나>, <둘>, '<하나> 힘센 이끼 / <둘> 거대한 이끼'",
                // 없는 tid
                "en, mfds, UNKNOWN, 1, 2, UNKNOWN",
        })
        @DisplayName("getMessage test - argument 포함 message 확인")
        void getMessageWithArgumentTest(final String languageCode, final String regulator,
                                        final String tid, final String arg1, final String arg2,
                                        final String expectedResult) {
            // given - i18nContext 설정
            I18nContext.setLanguage(languageCode);
            I18nContext.setRegulator(regulator);

            // when
            final String result = Assertions.assertDoesNotThrow(
                    () -> i18nMessageService.getMessage(tid, arg1, arg2)
            );

            // then
            Assertions.assertEquals(expectedResult, result);
        }
    }

    @Nested
    @DisplayName("reloadTests")
    class ReloadTests {
        @Test
        @DisplayName("reload test - reload 처리 성공")
        void reloadNormalTest() {
            // when, then
            Assertions.assertDoesNotThrow(i18nMessageService::reload);
        }

        @Test
        @DisplayName("reload test - exception 발생")
        void reloadExceptionTest() {
            // given - clearCache 처리시 exception 발생
            final ReloadableResourceBundleMessageSource mockMessageSource
                    = mock(ReloadableResourceBundleMessageSource.class);
            doThrow(UnsupportedOperationException.class).when(mockMessageSource).clearCache();

            final I18nMessageService localI18nMessageService = new I18nMessageService(
                    null,
                    Map.of(
                            "1", mock(ResourceBundleMessageSource.class),
                            "2", mockMessageSource
                    ),
                    null
            );

            // when
            final CommonRuntimeException exception
                    = Assertions.assertThrows(CommonRuntimeException.class, localI18nMessageService::reload);

            // then
            Assertions.assertEquals(CommonResultCode.INTERNAL_SERVER_ERROR, exception.getResultCode());
        }
    }
}
