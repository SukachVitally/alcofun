package com.alco.searchapp.properites;

import com.alco.searchapp.constants.ProviderTypeEnum;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.product.subscriber")
@Data
public class ProductSubscriberProperties {
    private Boolean enabled;

    private Map<ProviderTypeEnum, ProviderConfiguration> providers;

    @Data
    public static class ProviderConfiguration {
        private Integer delaySec = 0;
    }
}
