package com.web.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
@EnableCaching

public class RedisCacheConfig {
    @Bean
    public RedisCacheManager cacheManger(RedisConnectionFactory redisConnectionFactory){
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues();
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("categories",
                        defaultConfig.entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration("product",
                        defaultConfig.entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration("dashboard",
                        defaultConfig.entryTtl(Duration.ofSeconds(30)))
                .build();


    }
}
