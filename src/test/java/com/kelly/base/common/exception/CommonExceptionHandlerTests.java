package com.kelly.base.common.exception;

import com.kelly.base.common.dto.CommonResponse;
import com.kelly.base.common.enums.CommonResultCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.MethodParameter;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.mock;

@DisplayName("CommonExceptionHandlerTests")
class CommonExceptionHandlerTests {
    private CommonExceptionHandler commonExceptionHandler;

    @BeforeEach
    void init() {
        commonExceptionHandler = new CommonExceptionHandler();
    }

    @Nested
    @DisplayName("HandleCommonExceptionTests")
    class HandleCommonExceptionTests {
        @Test
        @DisplayName("handleCommonException test - CommonException with normal result code")
        void handleCommonExceptionNormalResultCodeTest() {
            // given
            final CommonResultCode expectedResultCode = CommonResultCode.INTERNAL_SERVER_ERROR;
            final CommonException commonException = new CommonException(expectedResultCode);

            // when
            final ResponseEntity<CommonResponse<Void>> result = Assertions.assertDoesNotThrow(
                    () -> commonExceptionHandler.handleCommonException(
                            new MockHttpServletRequest(),
                            commonException
                    )
            );

            // then - response
            Assertions.assertNotNull(result);

            // then - response body
            final CommonResponse<Void> responseBody = result.getBody();
            Assertions.assertNotNull(responseBody);
            Assertions.assertEquals(expectedResultCode.getCode(), responseBody.getCode());
            Assertions.assertEquals(expectedResultCode.getMessage(), responseBody.getMessage());
        }

        @ParameterizedTest
        @CsvSource({
                " , , 400, Bad Request",                            // null, null
                "94009999, , 94009999, Bad Request",                // 94009999, null
                " , Custom Error, 400, Custom Error",               // null, Custom Error
                "94009999, Custom Error, 94009999, Custom Error",   // 94009999, Custom Error
        })
        @DisplayName("handleCommonException test - CommonException with extra")
        void handleCommonExceptionExtraResultCodeTest(final Integer extraCode, final String extraMessage,
                                                      final Integer expectedCode, final String expectedMessage) {
            // given
            final CommonResultCode baseResultCode = CommonResultCode.BAD_REQUEST;
            final CommonException commonException = new CommonException(baseResultCode, extraCode, extraMessage);

            // when
            final ResponseEntity<CommonResponse<Void>> result = Assertions.assertDoesNotThrow(
                    () -> commonExceptionHandler.handleCommonException(
                            new MockHttpServletRequest(),
                            commonException
                    )
            );

            // then - response
            Assertions.assertNotNull(result);

            // then - response body
            final CommonResponse<Void> responseBody = result.getBody();
            Assertions.assertNotNull(responseBody);
            Assertions.assertEquals(expectedCode, responseBody.getCode());
            Assertions.assertEquals(expectedMessage, responseBody.getMessage());
        }

        @Test
        @DisplayName("handleCommonException test - CommonException with extra message only")
        void handleCommonExceptionExtraMessageOnlyResultCodeTest() {
            // given
            final CommonResultCode expectedResultCode = CommonResultCode.UNAUTHORIZED;
            final String expectedExtraMessage = "wrong user id";
            final CommonException commonException = new CommonException(expectedResultCode, expectedExtraMessage);

            // when
            final ResponseEntity<CommonResponse<Void>> result = Assertions.assertDoesNotThrow(
                    () -> commonExceptionHandler.handleCommonException(
                            new MockHttpServletRequest(),
                            commonException
                    )
            );

            // then - response
            Assertions.assertNotNull(result);

            // then - response body
            final CommonResponse<Void> responseBody = result.getBody();
            Assertions.assertNotNull(responseBody);
            Assertions.assertEquals(expectedResultCode.getCode(), responseBody.getCode());
            Assertions.assertEquals(expectedExtraMessage, responseBody.getMessage());   // message 만 변경됨
        }
    }

    @Nested
    @DisplayName("ParameterValidTests")
    class ParameterValidTests {
        @Test
        @DisplayName("handleMethodArgumentNotValidException test - by @Valid")
        void handleMethodArgumentNotValidExceptionTest() {
            // when
            final CommonResponse<Void> result = Assertions.assertDoesNotThrow(
                    () -> commonExceptionHandler.handleMethodArgumentNotValidException(
                            new MockHttpServletRequest(),
                            new MethodArgumentNotValidException(
                                    mock(MethodParameter.class), mock(BeanPropertyBindingResult.class)
                            )
                    )
            );

            // then
            Assertions.assertEquals(CommonResultCode.INVALID_PARAMETER.getCode(), result.getCode());
            Assertions.assertEquals(CommonResultCode.INVALID_PARAMETER.getMessage(), result.getMessage());
        }

        @Test
        @DisplayName("handleHandlerMethodValidationException test - by @Valid")
        void handleHandlerMethodValidationExceptionTest() {
            // when
            final CommonResponse<Void> result = Assertions.assertDoesNotThrow(
                    () -> commonExceptionHandler.handleHandlerMethodValidationException(
                            new MockHttpServletRequest(),
                            new HandlerMethodValidationException(mock(MethodValidationResult.class))
                    )
            );

            // then
            Assertions.assertEquals(CommonResultCode.INVALID_PARAMETER.getCode(), result.getCode());
            Assertions.assertEquals(CommonResultCode.INVALID_PARAMETER.getMessage(), result.getMessage());
        }

        @Test
        @DisplayName("handleConstraintViolationException test - by @NotNull")
        void handleConstraintViolationExceptionTest() {
            // given - violation mocking
            ConstraintViolation<?> violation = mock(ConstraintViolation.class);
            Set<ConstraintViolation<?>> violations = Collections.singleton(violation);

            // when
            final CommonResponse<Void> result = Assertions.assertDoesNotThrow(
                    () -> commonExceptionHandler.handleConstraintViolationException(
                            new MockHttpServletRequest(),
                            new ConstraintViolationException("NotNull error", violations)
                    )
            );

            // then
            Assertions.assertEquals(CommonResultCode.INVALID_PARAMETER.getCode(), result.getCode());
            Assertions.assertEquals(CommonResultCode.INVALID_PARAMETER.getMessage(), result.getMessage());
        }
    }

    @Nested
    @DisplayName("TaskErrorTests")
    class TaskErrorTests {
        @Test
        @DisplayName("handleTaskRejectedException test - by @Async exception")
        void handleTaskRejectedExceptionTest() {
            // when
            final CommonResponse<Void> result = Assertions.assertDoesNotThrow(
                    () -> commonExceptionHandler.handleTaskRejectedException(
                            new MockHttpServletRequest(),
                            new TaskRejectedException("async error")
                    )
            );

            // then
            Assertions.assertEquals(CommonResultCode.TASK_REJECTED.getCode(), result.getCode());
            Assertions.assertEquals(CommonResultCode.TASK_REJECTED.getMessage(), result.getMessage());
        }
    }

    @Nested
    @DisplayName("UndefinedExceptionTests")
    class UndefinedExceptionTests {
        @Test
        @DisplayName("handleException test - undefined")
        void unhandledExceptionTest() {
            // when
            final CommonResponse<Void> result = Assertions.assertDoesNotThrow(
                    () -> commonExceptionHandler.handleException(
                            new MockHttpServletRequest(),
                            mock(Exception.class)
                    )
            );

            // then
            Assertions.assertEquals(CommonResultCode.INTERNAL_SERVER_ERROR.getCode(), result.getCode());
            Assertions.assertEquals(CommonResultCode.INTERNAL_SERVER_ERROR.getMessage(), result.getMessage());
        }
    }
}
