package com.alco.searchapp.properites;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.product.search")
@Data
public class ProductSearchServiceProperties {
    private String index;
}
