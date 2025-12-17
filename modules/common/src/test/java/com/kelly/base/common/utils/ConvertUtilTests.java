package com.kelly.base.common.utils;

import com.kelly.base.common.exception.CommonException;
import com.kelly.base.common.response.CommonResultCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("ConvertUtilTests")
class ConvertUtilTests {
    @Nested
    @DisplayName("ConvertStr2LongTests")
    class ConvertStr2LongTests {
        @ParameterizedTest
        @ValueSource(strings = {
                "1", "0", "99999"
        })
        @DisplayName("convertStr2Long test - converted")
        void convertStr2LongConvertedTest(final String source) {
            // when, then
            Assertions.assertDoesNotThrow(
                    () -> ConvertUtil.convertStr2Long(source)
            );
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        @ValueSource(strings = {
                "1.1", "0.4"
        })
        @DisplayName("convertStr2Long test - exception")
        void convertStr2LongExceptionTest(final String source) {
            // when
            final CommonException e = Assertions.assertThrows(
                    CommonException.class,
                    () -> ConvertUtil.convertStr2Long(source)
            );

            // then
            Assertions.assertEquals(CommonResultCode.SYSTEM_ERROR, e.getResultCode());
        }
    }

    @Nested
    @DisplayName("ToLowerCaseTests")
    class ToLowerCaseTests {
        @ParameterizedTest
        @CsvSource({
                "toLowerCase, tolowercase",
                "123Number, 123number",
                ",",    // null 인 경우
                "'', ''",    // 빈문자열
        })
        @DisplayName("toLowerCase test")
        void toLowerCaseTest(final String source, final String expectedResult) {
            // when
            final String result = Assertions.assertDoesNotThrow(
                    () -> ConvertUtil.toLowerCase(source)
            );

            // then
            Assertions.assertEquals(expectedResult, result);
        }
    }
}
