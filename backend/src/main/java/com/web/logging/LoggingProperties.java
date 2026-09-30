package com.web.logging;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix="app.logging")
public class LoggingProperties {

    private Request request = new Request();

    @Getter
    @Setter
    public static class Request {
        private boolean enabled = true;
        private String requestIdHeader = "X-Request-ID";
        private Duration slowThreshold = Duration.ofSeconds(2);
        private List<String> excludePaths = new ArrayList<>();
    }
}
