package com.alco.searchapp.product;

import com.alco.searchapp.service.ElasticCollectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.product.refresher.enabled", havingValue = "true")
public class ProductRefresher {
    private final ElasticCollectService service;

    @Scheduled(cron = "${app.product.refresher.cron}")
    public void refresh() {
        service.refresh()
                .subscribe(productIds -> log.info("Product service was refreshed {} products", productIds.size()));
    }


}
