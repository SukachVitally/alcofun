package com.alco.searchapp.properites;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.product.subscriber")
@Data
public class ProductSubscriberProperties {
    private Boolean enabled = false;
}
