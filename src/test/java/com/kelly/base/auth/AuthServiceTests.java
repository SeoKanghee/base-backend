package com.kelly.base.auth;

import com.kelly.base.product.auth.AuthService;
import com.kelly.base.product.auth.dto.PostLoginRequest;
import com.kelly.base.product.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayName("AuthServiceTests")
class AuthServiceTests {
    @Autowired
    private UserRepository userRepository;

    private AuthService authService;

    @BeforeEach
    void init() {
        authService = new AuthService(userRepository);
    }

    @Nested
    @DisplayName("LoginTests")
    class LoginTests {
        @Test
        @DisplayName("login test - pass")
        void loginPassTest() {
            // given
            final PostLoginRequest postLoginRequest = new PostLoginRequest("id", "password");

            // when, then
            Assertions.assertDoesNotThrow(
                    () -> authService.login(postLoginRequest)
            );
        }
    }
}
