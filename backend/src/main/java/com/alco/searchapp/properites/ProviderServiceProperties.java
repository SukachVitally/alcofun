package com.alco.searchapp.properites;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app.provider-service")
public class ProviderServiceProperties {
    private Integer relevantTimeIntervalSec;
}
