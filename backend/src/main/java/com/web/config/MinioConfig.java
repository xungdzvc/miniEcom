package com.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.minio.MinioClient;

@Configuration
@ConditionalOnProperty(
        name = "app.storage.type",
        havingValue = "minio"
)
public class MinioConfig {
    @Bean
    public MinioClient minioClient(@Value("${minio.url}") String url,
                                   @Value("${minio.accessKey}") String accessKey,
                                   @Value("${minio.secretKey}") String secretKey) {


        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
}
