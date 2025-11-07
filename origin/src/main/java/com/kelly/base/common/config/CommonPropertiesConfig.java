package com.kelly.base.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "config.constants")
@Getter
@Setter
public class CommonPropertiesConfig {
    private String applicationName;             // application-name
    private String applicationVersion;          // application-version
}
