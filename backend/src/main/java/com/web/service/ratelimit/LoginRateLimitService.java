package com.web.service.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class LoginRateLimitService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;

    public boolean isBlocked(String ip, String username){
        String key = buildKey(ip,username);
        String value = redisTemplate.opsForValue().get(key);
        if(value == null){
            return false;
        }
        return Integer.parseInt(value) >= MAX_ATTEMPTS;
    }

    private String buildKey(String ip, String username){
        String normalizedUsename = username == null ? "uknown" : username.trim().toLowerCase();
        return "rate-limit:login" + ip + ":" + normalizedUsename;
    }

    public void loginFailed(String ip, String username){
        String key = buildKey(ip,username);
        Long attempts =
                redisTemplate.opsForValue().increment(key);

        if(attempts != null && attempts == 1){
            redisTemplate.expire(key,WINDOW);
        }
    }
    public void loginSuccess(String ip, String username){
        String key = buildKey(ip,username);
        redisTemplate.delete(key);
    }
}