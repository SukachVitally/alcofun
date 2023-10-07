package com.alco.searchapp.properites;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("minio")
public class MinioProperties {
    private String accessKey;

    private String secretKey;

    private String bucket;

    private String url;
}
