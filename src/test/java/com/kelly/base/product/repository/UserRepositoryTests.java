package com.kelly.base.product.repository;

import com.kelly.base.product.domain.users.User;
import com.kelly.base.product.domain.users.UserStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayName("UserRepositoryTests")
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("UserEntityTests")
    class UserEntityTests {
        @Test
        @DisplayName("updateProfile test")
        void updateProfileTest() {
            // given
            final User user = userRepository.findById(1L).orElseThrow();
            user.updateProfile("tester", "test team", "update profile");

            // when
            final User stored = Assertions.assertDoesNotThrow(
                    () -> userRepository.save(user)
            );

            // then
            Assertions.assertEquals(user.getName(), stored.getName());
            Assertions.assertEquals(user.getDepartment(), stored.getDepartment());
            Assertions.assertEquals(user.getMemo(), stored.getMemo());
        }

        @Test
        @DisplayName("changePassword test")
        void changePasswordTest() {
            // given
            final User user = userRepository.findById(1L).orElseThrow();
            user.changePassword("new_password");

            // when
            final User stored = Assertions.assertDoesNotThrow(
                    () -> userRepository.save(user)
            );

            // then
            Assertions.assertEquals(user.getPassword(), stored.getPassword());
        }

        @Test
        @DisplayName("updateStatus test")
        void updateStatusTest() {
            // given
            final User user = userRepository.findById(1L).orElseThrow();
            user.updateStatus(UserStatus.LOCKED);

            // when
            final User stored = Assertions.assertDoesNotThrow(
                    () -> userRepository.save(user)
            );

            // then
            Assertions.assertEquals(user.getStatus(), stored.getStatus());
        }

        @ParameterizedTest
        @CsvSource({
                "ACTIVE, true",
                "INACTIVE, false",
                "LOCKED, false",
                "DELETED, false",
        })
        @DisplayName("isActive test")
        void isActiveTest(final UserStatus newUserStatus, final boolean expectedResult) {
            // given
            final User user = userRepository.findById(1L).orElseThrow();
            user.updateStatus(newUserStatus);

            // when
            final User stored = Assertions.assertDoesNotThrow(
                    () -> userRepository.save(user)
            );

            // then
            Assertions.assertEquals(expectedResult, stored.isActive());
        }
    }
}
