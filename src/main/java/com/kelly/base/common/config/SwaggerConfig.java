package com.kelly.base.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.kelly.base.common.Constants.UrlInfo.*;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    // reference : https://springdoc.org/
    private final PropertiesConfig propertiesConfig;

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info().title("API definition - " + propertiesConfig.getApplicationName())
                .version(propertiesConfig.getApplicationVersion())
                .description("for Test");
        return new OpenAPI().info(info);
    }

    @Bean
    public GroupedOpenApi authOpenApi() {
        String[] paths = { URI_ROOT_AUTH + WITH_SUB_PATHS };
        return GroupedOpenApi.builder().group("auth").pathsToMatch(paths).build();
    }
}
