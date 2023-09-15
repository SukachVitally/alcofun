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
import reactor.core.scheduler.Schedulers;

@Slf4j
@ConditionalOnProperty(name = "app.image-service.storage", havingValue="MINIO")
@Component
@RequiredArgsConstructor
public class MinioStorage implements StorageInterface {
    private final MinioClient client;

    private final String tempDir = System.getProperty("java.io.tmpdir");

    @Override
    public Mono<Boolean> save(String bucket, String filename, byte[] content) {
        var filePath = tempDir + "/" + filename;
        File file = new File(filePath);

        return Mono.fromCallable(() -> {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(content);
                client.uploadObject(
                        UploadObjectArgs.builder()
                                .bucket(bucket)
                                .object(filename)
                                .filename(filePath)
                                .build());
                log.info(String.format("File %s was successfully uploaded.", filename));
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (file.exists() && file.delete()) {
                    log.debug("temporary file was removed");
                }
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
