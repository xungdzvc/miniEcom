package com.web.security.ratelimit;

import com.web.enums.RateLimitScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    private boolean enabled = false;
    private Map<String, Policy> policies = new HashMap<>();

    @Getter
    @Setter
    public static class Policy{
        private long limit;
        private Duration window;
        private RateLimitScope scope= RateLimitScope.USER_AND_IP;
        private String message = "too many request";
    }

    public void setPolicies(Map<String, Policy> policies) {
        this.policies = policies;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Map<String, Policy> getPolicies() {
        return policies;
    }
}
