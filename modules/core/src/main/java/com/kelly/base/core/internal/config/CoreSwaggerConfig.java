package com.kelly.base.core.internal.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.kelly.base.core.internal.Constants.UrlInfo.*;

@Configuration
public class CoreSwaggerConfig {
    @Bean
    public GroupedOpenApi systemOpenApi() {
        String[] paths = { URI_ROOT_SYSTEM + WITH_SUB_PATHS };
        return GroupedOpenApi.builder().group("system").pathsToMatch(paths).build();
    }
}
