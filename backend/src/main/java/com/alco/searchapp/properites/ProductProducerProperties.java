package com.alco.searchapp.properites;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.product.producer")
@Data
public class ProductProducerProperties {
    private Boolean enabled = false;

    private Integer threadCap = 1;

    private Integer queueCap = 1;

    private Integer delaySec = 5;

    private Integer intervalSec = 24 * 3600;
}
