package com.kelly.base.common.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static com.kelly.base.common.CommonConstants.CommonAsync.*;

@DisplayName("CommonAsyncConfigTests")
class CommonAsyncConfigTests {
    private final ApplicationContextRunner contextRunner
            = new ApplicationContextRunner().withUserConfiguration(CommonAsyncConfig.class);

    @Nested
    @DisplayName("InjectionTests")
    class InjectionTests {
        @Test
        @DisplayName("injection test - with true")
        void injectionTrueTest() {
            // when
            contextRunner.withPropertyValues("config.options.use-default-async=true").run(
                    context -> {
                        // when - commonAsync 를 사용하는 것으로 설정되어 있으므로 bean 이 생성됐는지 확인
                        Assertions.assertTrue(context.containsBean(COMMON_ASYNC_NAME));

                        // then - injection 이 됐는지 확인
                        final Executor commonAsync = context.getBean(Executor.class);
                        Assertions.assertNotNull(commonAsync);

                        // then - injection 된 bean 이 CommonAsync 인지 확인
                        final ThreadPoolTaskExecutor castedAsync
                                = Assertions.assertInstanceOf(ThreadPoolTaskExecutor.class, commonAsync);
                        Assertions.assertEquals(COMMON_ASYNC_THREAD_PREFIX, castedAsync.getThreadNamePrefix());
                    }
            );
        }

        @Test
        @DisplayName("injection test - none")
        void injectionNoneTest() {
            // when
            contextRunner.run(
                    context -> {
                        // when - 아무것도 설정되어 있지 않을 경우 default 로 생성되므로 bean 이 생성됐는지 확인
                        Assertions.assertTrue(context.containsBean(COMMON_ASYNC_NAME));

                        // then - injection 이 됐는지 확인
                        final Executor commonAsync = context.getBean(Executor.class);
                        Assertions.assertNotNull(commonAsync);

                        // then - injection 된 bean 이 CommonAsync 인지 확인
                        final ThreadPoolTaskExecutor castedAsync
                                = Assertions.assertInstanceOf(ThreadPoolTaskExecutor.class, commonAsync);
                        Assertions.assertEquals(COMMON_ASYNC_THREAD_PREFIX, castedAsync.getThreadNamePrefix());
                    }
            );
        }

        @Test
        @DisplayName("injection test - with false")
        void injectionFalseTest() {
            // when
            contextRunner.withPropertyValues("config.options.use-default-async=false").run(
                    context -> {
                        // when commonAsync 를 사용하지 않으므로 bean 이 생성되지 않았는지 확인
                        Assertions.assertFalse(context.containsBean(COMMON_ASYNC_NAME));

                        // then - Executor 로 선언된 bean 이 없는지 확인
                        Assertions.assertThrows(
                                NoSuchBeanDefinitionException.class,
                                () -> context.getBean(Executor.class)
                        );
                    }
            );
        }
    }
}