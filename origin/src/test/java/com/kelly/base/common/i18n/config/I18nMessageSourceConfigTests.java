package com.kelly.base.common.i18n.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelly.base.common.i18n.I18nProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("I18nMessageSourceConfigTests")
class I18nMessageSourceConfigTests {
    private ResourcePatternResolver mockResourceLoader;

    private I18nMessageSourceConfig i18nMessageSourceConfig;

    @BeforeEach
    void init() {
        mockResourceLoader = mock(ResourcePatternResolver.class);
        i18nMessageSourceConfig = new I18nMessageSourceConfig(
                mock(I18nProperties.class), mock(ObjectMapper.class), mockResourceLoader
        );
    }

    @Nested
    @DisplayName("regulatorMessageSourcesTests")
    class regulatorMessageSourcesTests {
        @Test
        @DisplayName("regulatorMessageSources test - 리소스 로딩 중 exception 발생")
        void regulatorMessageSourcesExceptionTest() throws IOException {
            // given
            final Resource[] mockingResult = getTestResources();
            when(mockResourceLoader.getResources(anyString())).thenReturn(mockingResult);

            // when - 반환된 3개의 리소스가 모두 처리 불가능
            final Map<String, MessageSource> result = Assertions.assertDoesNotThrow(
                    i18nMessageSourceConfig::regulatorMessageSources
            );

            // then - 빈상태로 초기화
            Assertions.assertTrue(result.isEmpty());
        }

        private Resource[] getTestResources() {
            // 1. 파일 이름이 없는 리소스
            final Resource firstResource = mock(Resource.class);
            when(firstResource.getFilename()).thenReturn(null);

            // 2. 파일 이름 포맷이 정의와 맞지 않는 리소스 -> messages_{규제기관}_{언어}.properties
            final Resource secondResource = mock(Resource.class);
            when(secondResource.getFilename()).thenReturn("2nd_messages.properties");

            // 3. null 리소스 포함하여 반환
            return new Resource[]{ firstResource, secondResource, null };
        }

        @Test
        @DisplayName("regulatorMessageSources test - ResourceLoader 가 잘못 injection 되어 처리 불가능")
        void regulatorMessageSourcesLoaderErrorTest() {
            // given
            I18nMessageSourceConfig localI18nMessageSourceConfig = new I18nMessageSourceConfig(
                    mock(I18nProperties.class), mock(ObjectMapper.class),
                    mock(ResourceLoader.class)  // ResourcePatternResolver 가 아님
            );

            // when
            final Map<String, MessageSource> result = Assertions.assertDoesNotThrow(
                    localI18nMessageSourceConfig::regulatorMessageSources
            );

            // then - 빈상태로 초기화
            Assertions.assertTrue(result.isEmpty());
        }
    }
}
