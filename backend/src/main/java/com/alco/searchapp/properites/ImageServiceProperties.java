package com.alco.searchapp.properites;

import com.alco.searchapp.constants.StorageEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app.image-service")
public class ImageServiceProperties {
    private String baseUrl;

    private StorageEnum storage;

    private String bucket;
}
