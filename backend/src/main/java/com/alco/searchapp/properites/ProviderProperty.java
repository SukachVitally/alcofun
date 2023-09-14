package com.alco.searchapp.properites;

import com.alco.searchapp.constants.ProviderTypeEnum;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.provider")
@Data
public class ProviderProperty {
    private List<ProviderTypeEnum> list;
}
