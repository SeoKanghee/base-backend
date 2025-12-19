package com.kelly.base.common.response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

@DisplayName("PagedResultTests")
class PagedResultTests {
    @Nested
    @DisplayName("ConstructorTests")
    class ConstructorTests {
        @Test
        @DisplayName("constructor test - with generic type")
        void constructorGenericTest() {
            // given
            final String genericType = "use string type";

            // when, then
            Assertions.assertDoesNotThrow(
                    () -> new PagedResult<>(
                            List.of(genericType), 1, 1, 0, 1,
                            false, false
                    )
            );
        }
    }
}
