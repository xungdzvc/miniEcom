package com.web.logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppInstanceProperties {
    @Value("${app.instance-id}")
    private String instanceId;

}
