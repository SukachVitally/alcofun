package com.alco.searchapp.product;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.properites.ProductSubscriberProperties;
import com.alco.searchapp.provider.dto.Product;
import com.alco.searchapp.provider.dto.ProductPageDto;
import java.time.Duration;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.product.subscriber.enabled", havingValue = "true")
@RequiredArgsConstructor
public class ProductSubscriber {
    private final ProductSubscribeFlow flow;

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

    @Bean
    public Consumer<Flux<ProductPageDto>> fozzyConsumer() {
        return createConsumer(ProviderTypeEnum.FOZZY);
    }

    private Consumer<Flux<ProductPageDto>> createConsumer(ProviderTypeEnum provider) {
        var configuration = properties.getProviders().get(provider);
        log.info("Start subscription for {}", provider.getName());
        return stream -> flow.apply(stream, provider, Duration.ofSeconds(configuration.getDelaySec()))
                .subscribe(this::handleProductInfo, this::handleConsumerError);
    }

    private void handleProductInfo(Product product) {
        log.info("[{}]: product {} was added", product.getProviderType().getName(), product.getInfo().getName());
    }

    private void handleConsumerError(Throwable e) {
        log.error("Consumer error: {}", e.getMessage(), e);
    }
}
