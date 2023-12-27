package com.alco.searchapp.properites.provider;

import java.util.List;

import com.alco.searchapp.properites.ProviderBlock;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("providers.alcomag")
public class AlcomagProperties {
    private List<ProviderBlock> blocks;
}
