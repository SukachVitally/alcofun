package com.alco.searchapp.properites;

import com.alco.searchapp.constants.ProviderTypeEnum;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.product.subscriber")
@Data
public class ProductSubscriberProperties {
    private Boolean enabled = false;

    private Integer delaySec = 0;

    private List<Provider> providers;

    @Data
    public static class Provider {
        private ProviderTypeEnum type;

        private Integer delaySec = 0;
    }
}
