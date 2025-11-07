package com.kelly.base.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ValueGeneratorTests")
class ValueGeneratorTests {
    @Nested
    @DisplayName("GetNanoIdTests")
    class GetNanoIdTests {
        @Test
        @DisplayName("getNanoId test")
        void getNanoIdTest() {
            // when
            final String result = Assertions.assertDoesNotThrow(ValueGenerator::getNanoId);

            // then
            Assertions.assertNotNull(result);
            Assertions.assertEquals(21, result.length());   // 기본 length 로 생성
        }
    }
}
