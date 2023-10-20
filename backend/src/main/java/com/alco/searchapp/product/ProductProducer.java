package com.alco.searchapp.product;

import com.alco.searchapp.properites.ProductProducerProperties;
import com.alco.searchapp.provider.ProviderCollector;
import com.alco.searchapp.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.product.producer.enabled", havingValue = "true")
@RequiredArgsConstructor
public class ProductProducer {
    private static final String THREAD_NAME = "ProductInfo collector";

    private final ProviderCollector collector;

    private final ProductProducerProperties properties;

    private final QueueService queueService;

    @Scheduled(cron = "${app.product.producer.cron}")
    public void init() {
        log.info("Start collecting products");
        Scheduler scheduler = Schedulers.newBoundedElastic(properties.getThreadCap(), properties.getQueueCap(), THREAD_NAME);
        collector.collect(properties.getProviders())
                .publishOn(scheduler)
                .flatMap(productPage -> queueService.sendProductPage(productPage)
                        .thenReturn(productPage))
                .subscribe(productPage -> log.info("Added url to queue {}", productPage.getUrl()));
    }
}
