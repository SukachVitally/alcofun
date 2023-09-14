package com.alco.searchapp.service;

import com.alco.searchapp.properites.CollectSchedulerProperty;
import com.alco.searchapp.provider.ProviderCollector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@ConditionalOnProperty(name = "app.scheduler.enable", havingValue = "true")
@RequiredArgsConstructor
public class CollectSchedulerService {
    private static final String THREAD_NAME = "Product collector";

    private final ProductService productService;

    private final ProviderCollector providerCollector;

    private final CollectSchedulerProperty property;

    @Scheduled(fixedDelay = 1000000)
    public void init() {
        log.info("Start collecting products");
        Scheduler scheduler = Schedulers.newBoundedElastic(property.getThreadCap(), property.getQueueCap(), THREAD_NAME);
        providerCollector.collect()
                .publishOn(scheduler)
                .subscribe(product -> productService.addProductInfo(product)
                        .subscribe(entity -> log.info(String.format("[%s]: product %s was added",
                                product.getProviderType().toString(), product.getName()))));
    }
}
