package com.alco.searchapp.product;

import com.alco.searchapp.dto.RatingDto;
import com.alco.searchapp.properites.ProductSubscriberProperties;
import com.alco.searchapp.provider.ProviderCollector;
import com.alco.searchapp.service.ProductService;
import com.alco.searchapp.service.QueueService;
import com.alco.searchapp.service.RatingService;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.product.subscriber.enabled", havingValue = "true")
@RequiredArgsConstructor
public class ProductSubscriber {
    private final QueueService queueService;

    private final ProductService productService;

    private final RatingService ratingService;

    private final ProviderCollector collector;

    private final ProductSubscriberProperties properties;

    /**
     * Start subscription method
     *
     * Configure global delay value just not to produce too many requests.
     */
    @PostConstruct
    public void subscribe() {
        log.info("Start add product subscription");
        queueService.receiveProductPages()
                .delayElements(Duration.ofSeconds(properties.getDelaySec()))
                .concatMap(collector::parse)
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
