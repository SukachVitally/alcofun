package com.alco.searchapp.service;

import com.alco.searchapp.dto.RatingDto;
import com.alco.searchapp.provider.ProviderCollector;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@ConditionalOnProperty(name = "app.scheduler.enable-subscription", havingValue = "true")
@RequiredArgsConstructor
public class ProductSubscriber {
    private final QueueService queueService;

    private final ProductService productService;

    private final RatingService ratingService;

    private final ProviderCollector collector;

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
