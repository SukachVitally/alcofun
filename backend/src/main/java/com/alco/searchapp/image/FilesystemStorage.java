package com.alco.searchapp.image;

import com.alco.searchapp.properites.FilesystemStorageProperty;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@ConditionalOnMissingBean(AwsS3Storage.class)
@RequiredArgsConstructor
public class FilesystemStorage implements StorageInterface {
    private final FilesystemStorageProperty property;

    @Override
    public Mono<Boolean> save(String path, byte[] data) {
        var filePath = String.format("%s%s", property.getPath(), path);
        return Mono.fromCallable(() -> writeDataToFile(filePath, data))
                .doOnTerminate(() -> log.info("File saved successfully: " + filePath));
    }

    private Boolean writeDataToFile(String filePath, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(data);
        }
        return true;
    }
}
