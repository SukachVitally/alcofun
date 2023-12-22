package com.alco.searchapp.properites;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.search")
@Data
public class SearchProperties {
    private String product;

    private String category;

    private String characteristic;
}
