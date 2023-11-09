package com.alco.searchapp.product;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.dto.RatingDto;
import com.alco.searchapp.properites.ProductSubscriberProperties;
import com.alco.searchapp.provider.ProviderCollector;
import com.alco.searchapp.provider.dto.ProductPageDto;
import com.alco.searchapp.service.ProductService;
import com.alco.searchapp.service.RatingService;
import java.time.Duration;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.product.subscriber.enabled", havingValue = "true")
@RequiredArgsConstructor
public class ProductSubscriber {
    private final ProductService productService;

    private final RatingService ratingService;

    private final ProviderCollector collector;

    private final ProductSubscriberProperties properties;

    @Bean
    public Consumer<Flux<ProductPageDto>> alcomagConsumer() {
        return createConsumer(ProviderTypeEnum.ALCOMAG);
    }

    @Bean
    public Consumer<Flux<ProductPageDto>> rozetkaConsumer() {
        return createConsumer(ProviderTypeEnum.ROZETKA);
    }

    @Bean
    public Consumer<Flux<ProductPageDto>> okwineConsumer() {
        return createConsumer(ProviderTypeEnum.OKWINE);
    }

    private Consumer<Flux<ProductPageDto>> createConsumer(ProviderTypeEnum provider) {
        var configuration = properties.getProviders().get(provider);
        log.info("Start for {}", provider.getName());
        return stream -> stream
                .filter(dto -> dto.getProvider().equals(provider))
                .delayElements(Duration.ofSeconds(configuration.getDelaySec()))
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
                .subscribe(product -> log.info("[{}]: product {} was added", product.getProviderType().getName(), product.getInfo().getName()),
                        e -> log.error("Consumer error: ", e));
    }
}
