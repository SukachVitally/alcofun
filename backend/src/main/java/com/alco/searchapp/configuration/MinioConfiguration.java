package com.alco.searchapp.configuration;

import com.alco.searchapp.properites.MinioProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.image-service.storage", havingValue="MINIO")
public class MinioConfiguration {
    private final MinioProperties properties;

    @Bean
    public MinioClient getMinioClient() {
        return MinioClient.builder()
                        .endpoint(properties.getUrl())
                        .credentials(properties.getAccessKey(), properties.getSecretKey())
                        .build();
    }
}
