package com.alco.searchapp.properites;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.filesystem-storage")
@Data
public class FilesystemStorageProperty {
    private String path;
}
