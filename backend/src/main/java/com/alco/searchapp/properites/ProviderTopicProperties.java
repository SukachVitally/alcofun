package com.alco.searchapp.properites;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app.provider-topic")
public class ProviderTopicProperties {
    private int partitions = 1;

    private short replicationFactor = 1;
}
