package com.alco.searchapp.service;

import com.alco.searchapp.dto.RatingDto;
import com.alco.searchapp.properites.CollectSchedulerProperties;
import com.alco.searchapp.provider.ProviderCollector;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@ConditionalOnProperty(name = "app.scheduler.enable", havingValue = "true")
@RequiredArgsConstructor
public class CollectSchedulerService {
    private static final String THREAD_NAME = "ProductInfo collector";

    private final ProductService productService;

    private final RatingService ratingService;

    private final ProviderCollector collector;

    private final CollectSchedulerProperties properties;

    private final QueueService queueService;

    @Scheduled(fixedDelay = 1000000)
    public void init() {
        log.info("Start collecting products");
        Scheduler scheduler = Schedulers.newBoundedElastic(properties.getThreadCap(), properties.getQueueCap(), THREAD_NAME);
        collector.collect()
                .publishOn(scheduler)
                .flatMap(queueService::sendProductPage)
                .subscribe(i -> log.info("Added url to queue {}", i.topic()));

    }

    @PostConstruct
    public void subscribe() {
        log.info("Start add product subscription");
        queueService.receiveProductPages()
                .flatMap(collector::parse)
                .flatMap(product -> productService.addProduct(product)
                        .flatMap(entity -> {
                            if (product.getInfo().getRating() == null
                                    || product.getInfo().getVotes() == null
                                    || product.getInfo().getVotes() == 0) {
                                return Mono.just(entity);
                            }
                            var rating = new RatingDto(product.getInfo().getRating(), product.getInfo().getVotes());
                            return ratingService.addRating(entity.getProductId(), entity.getProviderId(), rating)
                                .then(Mono.just(entity));
                        })
                        .thenReturn(product))
                .subscribe(product -> log
                        .info("[{}]: product {} was added", product.getProviderType().getName(),
                                product.getInfo().getName()));

    }
}
