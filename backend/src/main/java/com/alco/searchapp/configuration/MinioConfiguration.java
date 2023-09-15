package com.alco.searchapp.configuration;

import com.alco.searchapp.properites.MinioProperty;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
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
