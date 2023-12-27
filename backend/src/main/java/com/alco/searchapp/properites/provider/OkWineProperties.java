package com.alco.searchapp.properites.provider;

import java.util.List;

import com.alco.searchapp.properites.ProviderBlock;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("providers.okwine")
public class OkWineProperties {
    private List<ProviderBlock> blocks;
}
