package com.alco.searchapp.properites;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app.queue")
public class QueueProperties {
    private String productTopic;
}
