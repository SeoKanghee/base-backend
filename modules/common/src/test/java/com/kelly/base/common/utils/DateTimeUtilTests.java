package com.kelly.base.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

class DateTimeUtilTests {
    @Nested
    @DisplayName("NowUtcPlusMinutesTests")
    class NowUtcPlusMinutesTests {
        @Test
        @DisplayName("nowUtcPlusMinutes test")
        void nowUtcPlusMinutesTest() {
            // when, then
            final ZonedDateTime result = Assertions.assertDoesNotThrow(
                    () -> DateTimeUtil.nowUtcPlusMinutes(10)
            );

            // 테스트 결과를 로그로 확인하기 위한 출력
            System.out.println("after 10 minutes: " + result);
        }
    }

    @Nested
    @DisplayName("NowUtcMinusMinutesTests")
    class NowUtcMinusMinutesTests {
        @Test
        @DisplayName("nowUtcMinusMinutes test")
        void nowUtcMinusMinutesTest() {
            // when, then
            final ZonedDateTime result = Assertions.assertDoesNotThrow(
                    () -> DateTimeUtil.nowUtcMinusMinutes(10)
            );

            // 테스트 결과를 로그로 확인하기 위한 출력
            System.out.println("before 10 minutes: " + result);
        }
    }
}
