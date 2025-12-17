package com.kelly.base.identity.internal.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.kelly.base.common.CommonConstants.UrlInfo.*;

@Configuration
public class IdentitySwaggerConfig {
    @Bean
    public GroupedOpenApi authOpenApi() {
        String[] paths = { URI_ROOT_AUTH + WITH_SUB_PATHS };
        return GroupedOpenApi.builder().group("auth").pathsToMatch(paths).build();
    }

    @Bean
    public GroupedOpenApi accountsOpenApi() {
        String[] paths = { URI_ROOT_ACCOUNTS + WITH_SUB_PATHS };
        return GroupedOpenApi.builder().group("accounts").pathsToMatch(paths).build();
    }
}
