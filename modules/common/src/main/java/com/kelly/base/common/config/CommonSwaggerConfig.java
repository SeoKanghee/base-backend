package com.kelly.base.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CommonSwaggerConfig {
    // reference : https://springdoc.org/
    private final PropertiesConfig propertiesConfig;

    @Bean
    @ConditionalOnMissingBean(OpenAPI.class)
    public OpenAPI openAPI() {
        Info info = new Info().title("API definition - " + propertiesConfig.getApplicationName())
                              .version(propertiesConfig.getApplicationVersion())
                              .description("for Test");
        return new OpenAPI().info(info);
    }
}
