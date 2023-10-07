package com.alco.searchapp.properites;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("elasticsearch")
@Data
public class ElasticsearchProperties {
    private String url;
}
