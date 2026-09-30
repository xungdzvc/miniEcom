package com.web.security.ratelimit;

public record RateLimitResult(
        boolean allowed,
        long current,
        long remaining,
        long retryAfterSeconds
) {
}
