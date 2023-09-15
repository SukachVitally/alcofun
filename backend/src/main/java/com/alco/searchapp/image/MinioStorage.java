package com.alco.searchapp.image;

import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@ConditionalOnProperty(name = "aws.s3.bucket")
@Component
@RequiredArgsConstructor
public class MinioStorage implements StorageInterface {
    private final MinioClient client;

    private final String tempDir = System.getProperty("java.io.tmpdir");

    @Override
    public Mono<Boolean> save(String bucket, String filename, byte[] content) {
        var filePath = tempDir + "/" + filename;

        File file = new File(filePath);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content);
            client.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .filename(filePath)
                            .build());
            return Mono.just(true);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error: " + e.getMessage());
        } finally {
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("File deleted successfully.");
                } else {
                    System.err.println("Failed to delete the file.");
                }
            }
        }

        return Mono.just(false);
    }
}
