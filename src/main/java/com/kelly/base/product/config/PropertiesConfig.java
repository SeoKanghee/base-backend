package com.kelly.base.product.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "config.constants")
@Getter
@Setter
public class PropertiesConfig {
    private String applicationName;             // application-name
    private String applicationVersion;          // application-version
}
