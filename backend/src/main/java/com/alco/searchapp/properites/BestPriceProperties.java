package com.alco.searchapp.properites;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app.best-price")
public class BestPriceProperties {
    private Integer minPercent;
}
