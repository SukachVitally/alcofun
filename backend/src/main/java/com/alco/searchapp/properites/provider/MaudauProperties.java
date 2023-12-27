package com.alco.searchapp.properites.provider;

import com.alco.searchapp.properites.ProviderBlock;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties("providers.maudau")
public class MaudauProperties {
    private List<ProviderBlock> blocks;
}
