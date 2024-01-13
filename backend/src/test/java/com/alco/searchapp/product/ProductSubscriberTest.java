package com.alco.searchapp.product;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.provider.dto.ProductPageDto;
import java.time.Duration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest()
public class ProductSubscriberTest extends BaseFunctionalTest {
    @Autowired
    private ProductSubscribeFlow subscriber;

    @Test
    public void parseLinkInvalidUrl() {
        var dto = ProductPageDto.builder()
                .url("invalid_url")
                .provider(ProviderTypeEnum.ALCOMAG)
                .category(CategoryTypeEnum.RUM)
                .build();

        Flux<ProductPageDto> testFlux = Flux.just(dto);
        StepVerifier.create(subscriber.apply(testFlux, dto.getProvider(), Duration.ofSeconds(0)))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void parseLinkInvalidAndCorrectUrl() {
        var dto1 = ProductPageDto.builder()
                .url("invalid_url")
                .provider(ProviderTypeEnum.ALCOMAG)
                .category(CategoryTypeEnum.RUM)
                .build();
        var dto2 = ProductPageDto.builder()
                .url("https://alcomag.ua/ua/rom-havana-club-anejo-7-anos-0-7-l/")
                .provider(ProviderTypeEnum.ALCOMAG)
                .category(CategoryTypeEnum.RUM)
                .build();


        Flux<ProductPageDto> testFlux = Flux.just(dto1, dto2);
        StepVerifier.create(subscriber.apply(testFlux, dto2.getProvider(), Duration.ofSeconds(0)))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void parseLink() {
        var dto = ProductPageDto.builder()
                .url("https://alcomag.ua/ua/rom-havana-club-anejo-7-anos-0-7-l/")
                .provider(ProviderTypeEnum.ALCOMAG)
                .category(CategoryTypeEnum.RUM)
                .build();

        Flux<ProductPageDto> testFlux = Flux.just(dto);
        StepVerifier.create(subscriber.apply(testFlux, dto.getProvider(), Duration.ofSeconds(0)))
                .consumeNextWith(product -> Assertions.assertEquals("Ром Havana Club Anejo 7 Anos 0.7 л",
                        product.getInfo().getName()))
                .verifyComplete();
    }
}
