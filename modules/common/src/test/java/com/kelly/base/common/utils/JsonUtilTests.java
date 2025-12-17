package com.kelly.base.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.kelly.base.common.exception.CommonException;
import com.kelly.base.common.exception.CommonRuntimeException;
import com.kelly.base.common.response.CommonResultCode;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

@DisplayName("JsonUtilTests")
class JsonUtilTests {
    @Nested
    @DisplayName("ConvertTests")
    class ConvertTests {
        static class SerializerError {
            // serialize 를 위해 getter 를 호출할 때 exception 발생
            public String getValue() {
                throw new CommonRuntimeException(CommonResultCode.INTERNAL_SERVER_ERROR);
            }
        }

        @Test
        @DisplayName("convert test - null")
        void convertParsingNullTest() {
            // when
            final String result = Assertions.assertDoesNotThrow(
                    () -> JsonUtil.convert(null, true)
            );

            // then - null 인 경우 String "null" 이 아니라 null 이 반환되는지 확인
            Assertions.assertNull(result);
        }

        @ParameterizedTest
        @CsvSource({
                "true, { }",    // pretty 적용시 빈 객체 출력
                "false, {}",    // pretty 미적용시 빈 객체 출력
        })
        @DisplayName("convert test - empty")
        void convertParsingEmptyTest(final boolean isPretty, final String expectedResult) {
            // when
            final String result = Assertions.assertDoesNotThrow(
                    () -> JsonUtil.convert(new Object(), isPretty)
            );

            // then
            Assertions.assertEquals(expectedResult, result);
        }

        @ParameterizedTest
        @ValueSource(booleans = { true, false })
        @DisplayName("convert test - converting error")
        void convertConvertingErrorTest(final boolean isPretty) {
            // given
            final SerializerError serializerError = new SerializerError();

            // when
            final CommonException e = Assertions.assertThrows(
                    CommonException.class,
                    () -> JsonUtil.convert(serializerError, isPretty)
            );

            // then - pretty 적용 여부와 상관 없이 동일한 exception 발생
            Assertions.assertEquals(CommonResultCode.SYSTEM_ERROR, e.getResultCode());
        }
    }

    @Nested
    @DisplayName("ParseTests")
    class ParseTests {
        @Getter
        static class InnerDto {
            private String key;
            private String value;
        }

        @Test
        @DisplayName("parse test - parsed by class")
        void parseClassTest() {
            // given
            final String rawData = """
                                { "key" : "testKey", "value" : "testValue" }
                    """;

            // when
            final InnerDto result = Assertions.assertDoesNotThrow(
                    () -> JsonUtil.parse(rawData, InnerDto.class)
            );

            // then
            Assertions.assertEquals("testKey", result.getKey());
            Assertions.assertEquals("testValue", result.getValue());
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {
                "{ \"value\" : \"not closed\"",
        })
        @DisplayName("parse test - parsing error by class")
        void parseClassErrorTest(final String rawData) {
            // when - 1. null / 2. 잘못된 데이터
            final CommonException e = Assertions.assertThrows(
                    CommonException.class,
                    () -> JsonUtil.parse(rawData, InnerDto.class)
            );

            // then
            Assertions.assertEquals(CommonResultCode.SYSTEM_ERROR, e.getResultCode());
        }

        @Test
        @DisplayName("parse test - parsed by type reference")
        void parseTypeReferenceTest() {
            // given
            final String rawData = """
                                [{ "key" : "testKey", "value" : "testValue" }]
                    """;
            final TypeReference<List<InnerDto>> typeRef = new TypeReference<>() {
            };

            // when
            final List<InnerDto> resultList = Assertions.assertDoesNotThrow(
                    () -> JsonUtil.parse(rawData, typeRef)
            );

            // then
            Assertions.assertEquals(1, resultList.size());

            final InnerDto result = resultList.get(0);
            Assertions.assertEquals("testKey", result.getKey());
            Assertions.assertEquals("testValue", result.getValue());
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = {
                "{ \"value\" : \"single dto\" }",
        })
        @DisplayName("parse test - parsing error by type reference")
        void parseTypeReferenceErrorTest(final String rawData) {
            // given
            final TypeReference<List<InnerDto>> typeRef = new TypeReference<>() {
            };

            // when - 1. null / 2. 잘못된 데이터
            final CommonException e = Assertions.assertThrows(
                    CommonException.class,
                    () -> JsonUtil.parse(rawData, typeRef)
            );

            // then
            Assertions.assertEquals(CommonResultCode.SYSTEM_ERROR, e.getResultCode());
        }
    }
}
