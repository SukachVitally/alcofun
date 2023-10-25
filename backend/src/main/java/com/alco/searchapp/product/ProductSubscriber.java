package com.alco.searchapp.product;

import com.alco.searchapp.constants.TopicConstants;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.dto.RatingDto;
import com.alco.searchapp.properites.ProductSubscriberProperties;
import com.alco.searchapp.provider.ProviderCollector;
import com.alco.searchapp.provider.dto.ProductPageDto;
import com.alco.searchapp.service.ProductService;
import com.alco.searchapp.service.RatingService;
import com.alco.searchapp.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.product.subscriber.enabled", havingValue = "true")
@RequiredArgsConstructor
public class ProductSubscriber {
    private final static String GROUP_ID = "product-consumer";

    private final ProductService productService;

    private final RatingService ratingService;

    private final ProviderCollector collector;

    private final ProductSubscriberProperties properties;

    @KafkaListener(topics = TopicConstants.PRODUCT_ALCOMAG, groupId = GROUP_ID)
    public void listenAlcomag(ProductPageDto dto) {
        collectInfo(dto);
        TimeUtils.doDelay(properties.getProviders().get(ProviderTypeEnum.ALCOMAG).getDelaySec());
    }

    @KafkaListener(topics = TopicConstants.PRODUCT_ROZETKA, groupId = GROUP_ID)
    public void listenRozetka(ProductPageDto dto) {
        collectInfo(dto);
        TimeUtils.doDelay(properties.getProviders().get(ProviderTypeEnum.ROZETKA).getDelaySec());
    }

    @KafkaListener(topics = TopicConstants.PRODUCT_OKWINE, groupId = GROUP_ID)
    public void listenOkWine(ProductPageDto dto) {
        collectInfo(dto);
        TimeUtils.doDelay(properties.getProviders().get(ProviderTypeEnum.OKWINE).getDelaySec());
    }

    private void collectInfo(ProductPageDto dto) {
        Flux.just(dto)
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
