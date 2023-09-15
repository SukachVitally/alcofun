package com.alco.searchapp.configuration;

import com.alco.searchapp.properites.MinioProperty;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.image-service.storage", havingValue="MINIO")
public class MinioConfiguration {
    private final MinioProperty property;

    @Bean
    public MinioClient getMinioClient() {
        return MinioClient.builder()
                        .endpoint(property.getUrl())
                        .credentials(property.getAccessKey(), property.getSecretKey())
                        .build();
    }
}
