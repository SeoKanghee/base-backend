package com.kelly.base.common.dto;

import com.kelly.base.common.enums.CommonResultCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CommonResponseTests")
class CommonResponseTests {
    @Nested
    @DisplayName("ConstructorTests")
    class ConstructorTests {
        @Test
        @DisplayName("constructor test - with generic type")
        void constructorGenericTest() {
            // given
            final CommonResultCode expectedResultCode = CommonResultCode.SUCCESS;
            final String expectedInternalResult = "internal result";

            // when
            final CommonResponse<String> result = Assertions.assertDoesNotThrow(
                    () -> new CommonResponse<>(expectedResultCode, expectedInternalResult)
            );

            // then
            Assertions.assertEquals(expectedResultCode.getCode(), result.getCode());
            Assertions.assertEquals(expectedResultCode.getMessage(), result.getMessage());
            Assertions.assertEquals(expectedInternalResult, result.getResult());
        }
    }
}
