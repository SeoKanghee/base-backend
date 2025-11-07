package com.kelly.base.common.exception;

import com.kelly.base.common.response.CommonResultCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

@DisplayName("CommonRuntimeExceptionTests")
class CommonRuntimeExceptionTests {
    @Nested
    @DisplayName("ConstructorTests")
    class ConstructorTests {
        @Test
        @DisplayName("constructor test - default")
        void constructorDefaultTest() {
            // given
            final NullPointerException uncheckedException = mock(NullPointerException.class);

            // when
            final CommonRuntimeException result = Assertions.assertDoesNotThrow(
                    () -> new CommonRuntimeException(uncheckedException)
            );

            // then
            Assertions.assertEquals("NullPointerException", result.getCause().getClass().getSimpleName());
            Assertions.assertEquals(CommonResultCode.INTERNAL_SERVER_ERROR, result.getResultCode());
            Assertions.assertNull(result.getExtraCode());
            Assertions.assertNull(result.getExtraMessage());
        }

        @Test
        @DisplayName("constructor test - result code")
        void constructor1ParamTest() {
            // given
            final CommonResultCode resultCode = CommonResultCode.SUCCESS;

            // when
            final CommonRuntimeException result = Assertions.assertDoesNotThrow(
                    () -> new CommonRuntimeException(resultCode)
            );

            // then
            Assertions.assertEquals(CommonResultCode.SUCCESS, result.getResultCode());
            Assertions.assertNull(result.getExtraCode());
            Assertions.assertNull(result.getExtraMessage());
        }

        @Test
        @DisplayName("constructor test - result code, extra message")
        void constructor2ParamTest() {
            // given
            final CommonResultCode resultCode = CommonResultCode.SUCCESS;
            final String extraMessage = "extra message";

            // when
            final CommonRuntimeException result = Assertions.assertDoesNotThrow(
                    () -> new CommonRuntimeException(resultCode, extraMessage)
            );

            // then
            Assertions.assertEquals(CommonResultCode.SUCCESS, result.getResultCode());
            Assertions.assertNull(result.getExtraCode());
            Assertions.assertEquals(extraMessage, result.getExtraMessage());
        }

        @Test
        @DisplayName("constructor test - result code, extra code, extra message")
        void constructor3ParamTest() {
            // given
            final CommonResultCode resultCode = CommonResultCode.SUCCESS;
            final Integer extraCode = 79;
            final String extraMessage = "extra message";

            // when
            final CommonRuntimeException result = Assertions.assertDoesNotThrow(
                    () -> new CommonRuntimeException(resultCode, extraCode, extraMessage)
            );

            // then
            Assertions.assertEquals(CommonResultCode.SUCCESS, result.getResultCode());
            Assertions.assertEquals(extraCode, result.getExtraCode());
            Assertions.assertEquals(extraMessage, result.getExtraMessage());
        }
    }
}
