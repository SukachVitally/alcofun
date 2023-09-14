package com.alco.searchapp.image;

import com.alco.searchapp.properites.AwsS3Property;
import java.nio.ByteBuffer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ConditionalOnProperty(name = "aws.s3.bucket")
@Component
@RequiredArgsConstructor
public class AwsS3Storage implements StorageInterface {
    private final AwsS3Property properties;

    private final S3AsyncClient s3Client;

    @Override
    public Mono<Boolean> save(String path, byte[] content) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(path)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        return Mono.fromFuture(s3Client.putObject(request, AsyncRequestBody.fromByteBuffer(ByteBuffer.wrap(content))))
                .map(response -> response.sdkFields().size() > 0);
    }
}
