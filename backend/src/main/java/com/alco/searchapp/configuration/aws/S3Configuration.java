package com.alco.searchapp.configuration.aws;

import com.alco.searchapp.properites.AwsS3Property;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;

@ConditionalOnProperty(name = "aws.s3.bucket")
@Configuration
@RequiredArgsConstructor
public class S3Configuration {
    private final AwsS3Property s3ConfigProperties;

    @Bean
    public S3AsyncClient s3AsyncClient() {
        return S3AsyncClient.builder()
                .httpClient(sdkAsyncHttpClient())
                .region(Region.of(s3ConfigProperties.getRegion()))
                .credentialsProvider(() -> AwsBasicCredentials.create(
                        s3ConfigProperties.getAccessKey(), s3ConfigProperties.getSecretKey()))
                .forcePathStyle(true)
                .serviceConfiguration(s3Configuration()).build();
    }

    private SdkAsyncHttpClient sdkAsyncHttpClient() {
        return NettyNioAsyncHttpClient.builder()
                .writeTimeout(Duration.ZERO)
                .build();
    }

    private software.amazon.awssdk.services.s3.S3Configuration s3Configuration() {
        return software.amazon.awssdk.services.s3.S3Configuration.builder()
                .checksumValidationEnabled(false)
                .chunkedEncodingEnabled(true)
                .build();
    }
}
