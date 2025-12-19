package com.kelly.base.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static com.kelly.base.common.CommonConstants.CommonAsync.*;

@Configuration
@EnableAsync
@ConditionalOnProperty(
        name = "config.options.use-default-async", // bean 생성 여부를 결정할 property
        havingValue = "true",   // 해당 property 값이 true 면 생성
        matchIfMissing = true   // property 를 따로 선언해주지 않을 경우 기본으로 그냥 생성
)
public class CommonAsyncConfig {
    @Bean(name = COMMON_ASYNC_NAME)
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(COMMON_ASYNC_CORE_POOL_SIZE);
        executor.setQueueCapacity(COMMON_ASYNC_QUEUE_CAPACITY);
        executor.setMaxPoolSize(COMMON_ASYNC_MAX_POOL_SIZE);
        executor.setThreadNamePrefix(COMMON_ASYNC_THREAD_PREFIX);
        executor.initialize();
        return executor;
    }
}
