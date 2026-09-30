package com.web.security.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class RateLimitService {
    private final StringRedisTemplate redisTemplate;
    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT;

    static{
        RATE_LIMIT_SCRIPT = new DefaultRedisScript<>();
        RATE_LIMIT_SCRIPT.setScriptText("""
            local current = redis.call('INCR', KEYS[1])

            if current == 1 then
                redis.call('EXPIRE', KEYS[1], ARGV[1])
            end

            return current
            """);

        RATE_LIMIT_SCRIPT.setResultType(Long.class);
    }
    public RateLimitResult consume(String key, long limit, Duration window){
        long windowSeconds = Math.max(1,window.toSeconds());
        Long current = redisTemplate.execute(RATE_LIMIT_SCRIPT, Collections.singletonList(key),String.valueOf(windowSeconds));
        if (current == null) {
            throw new IllegalStateException(
                    "Redis rate limit returned null"
            );
        }
        Long ttl = redisTemplate.getExpire(key);
        long retryAffter = ttl != null && ttl > 0 ? ttl : windowSeconds;
        boolean allowed = current <= limit;
        long remaining = Math.max(0,limit - current);

        return new RateLimitResult(allowed,current,remaining,allowed ? 0 : retryAffter);
    }


}
