package com.alco.searchapp.product;

import com.alco.searchapp.properites.ProductProducerProperties;
import com.alco.searchapp.provider.ProviderCollector;
import com.alco.searchapp.provider.dto.ProductPageDto;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@ConditionalOnProperty(name = "app.product.producer.enabled", havingValue = "true")
@RequiredArgsConstructor
public class ProductPublisher {
    private static final String THREAD_NAME = "ProductInfo collector";

    private final ProviderCollector collector;

    private final ProductProducerProperties properties;

    private final Sinks.Many<ProductPageDto> alcomagSink = createSink();

    private final Sinks.Many<ProductPageDto> okwineSink = createSink();

    private final Sinks.Many<ProductPageDto> fozzySink = createSink();

    @Scheduled(cron = "${app.product.producer.cron}")
    public void init() {
        log.info("Start collecting products");
        Scheduler scheduler = Schedulers.newBoundedElastic(properties.getThreadCap(), properties.getQueueCap(), THREAD_NAME);
        collector.collect(properties.getProviders())
                .publishOn(scheduler)
                .doOnNext(page -> log.info("Add product page {}", page.getUrl()))
                .subscribe(this::publish);
    }

    @Bean
    public Supplier<Flux<ProductPageDto>> alcomagSupplier() {
        return alcomagSink::asFlux;
    }

    @Bean
    public Supplier<Flux<ProductPageDto>> okwineSupplier() {
        return okwineSink::asFlux;
    }

    @Bean
    public Supplier<Flux<ProductPageDto>> fozzySupplier() {
        return okwineSink::asFlux;
    }

    private static Sinks.Many<ProductPageDto> createSink() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }

    private void publish(ProductPageDto message) {
        switch (message.getProvider()) {
            case ALCOMAG -> alcomagSink.emitNext(message, Sinks.EmitFailureHandler.FAIL_FAST);
            case OKWINE -> okwineSink.emitNext(message, Sinks.EmitFailureHandler.FAIL_FAST);
            case FOZZY -> fozzySink.emitNext(message, Sinks.EmitFailureHandler.FAIL_FAST);
        }
    }

}
