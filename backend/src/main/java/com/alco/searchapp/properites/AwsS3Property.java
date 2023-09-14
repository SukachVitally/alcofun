package com.alco.searchapp.properites;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("aws.s3")
public class AwsS3Property {
    private String accessKey;

    private String secretKey;

    private String bucket;

    private String region;
}
