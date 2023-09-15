package com.alco.searchapp.image;

import java.nio.ByteBuffer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ConditionalOnProperty(name = "app.image-service.storage", havingValue="S3")
@Component
@RequiredArgsConstructor
public class AwsS3Storage implements StorageInterface {
    private final S3AsyncClient s3Client;

    @Override
    public Mono<Boolean> save(String bucket, String path, byte[] content) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(path)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        return Mono.fromFuture(s3Client.putObject(request, AsyncRequestBody.fromByteBuffer(ByteBuffer.wrap(content))))
                .map(response -> response.sdkFields().size() > 0);
    }
}
