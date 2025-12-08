package com.kelly.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BaseBackendApplicationTests")
class BaseBackendApplicationTests {
    @Test
    @DisplayName("main application test")
    void bootRunTest() {
        Assertions.assertDoesNotThrow(
                () -> BaseBackendApplication.main(new String[] {})
        );
    }
}
