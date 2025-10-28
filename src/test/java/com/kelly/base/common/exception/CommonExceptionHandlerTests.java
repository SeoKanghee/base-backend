package com.kelly.base.common.exception;

import com.kelly.base.common.dto.CommonResponse;
import com.kelly.base.common.enums.CommonResultCode;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

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
    }
}
