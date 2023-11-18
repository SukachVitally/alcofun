package com.alco.searchapp.product;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.dto.RatingDto;
import com.alco.searchapp.provider.ProviderCollector;
import com.alco.searchapp.provider.dto.Product;
import com.alco.searchapp.provider.dto.ProductPageDto;
import com.alco.searchapp.service.ProductService;
import com.alco.searchapp.service.RatingService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSubscribeFlow {
    private final ProductService productService;

    private final RatingService ratingService;

    private final ProviderCollector collector;

    public Flux<Product> apply(Flux<ProductPageDto> flux, ProviderTypeEnum provider, Duration delay) {
        return flux
                .filter(dto -> dto.getProvider().equals(provider))
                .delayElements(delay)
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
                .doOnError(e -> log.error("Error {} in {} provider", e.getMessage(), provider.getName()))
                .onErrorResume(e -> Flux.empty());
    }
}
