package com.alco.searchapp.service;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.dto.ProductProviderDto;
import com.alco.searchapp.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest()
public class BestPriceServiceTest extends BaseFunctionalTest {
    @Autowired
    private BestPriceService service;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProviderService providerService;

    @Test
    public void getBestPriceWithoutHot() {
        addProducts();
        var product = productRepository.findOneByEan(EAN1).block();
        Assertions.assertNotNull(product);
        var bestPrice = providerService.getRelevantProducts(product.getId())
                .flatMap(providers -> service.getBestPrice(product.getId(), providers))
                .block();

        Assertions.assertNotNull(bestPrice);
        Assertions.assertEquals(100d, bestPrice.getPrice());
        Assertions.assertEquals(0d, bestPrice.getPercent());
        Assertions.assertFalse(bestPrice.getHot());
    }

    @Test
    public void getBestPriceWithHot() {
        addProducts();
        var product = productRepository.findOneByEan(EAN1).block();
        Assertions.assertNotNull(product);
        providerService.getRelevantProducts(product.getId())
                .flatMap(providers -> service.getBestPrice(product.getId(), providers))
                .block();

        var bestPrice = providerService.getRelevantProducts(product.getId())
                .map(providers -> providers.stream()
                        .map(dto -> ProductProviderDto.builder()
                                .price(dto.getPrice() - 50d)
                                .providerId(dto.getProviderId())
                                .build())
                        .toList())
                .flatMap(providers -> service.getBestPrice(product.getId(), providers))
                .block();

        Assertions.assertNotNull(bestPrice);
        Assertions.assertEquals(50d, bestPrice.getPrice());
        Assertions.assertEquals(50d, bestPrice.getPercent());
        Assertions.assertTrue(bestPrice.getHot());
    }

    @Test
    public void getBestPriceWithSameHot() {
        addProducts();
        var product = productRepository.findOneByEan(EAN1).block();
        Assertions.assertNotNull(product);
        providerService.getRelevantProducts(product.getId())
                .flatMap(providers -> service.getBestPrice(product.getId(), providers))
                .block();

        providerService.getRelevantProducts(product.getId())
                .map(providers -> providers.stream()
                        .map(dto -> ProductProviderDto.builder()
                                .price(dto.getPrice() - 50d)
                                .providerId(dto.getProviderId())
                                .build())
                        .toList())
                .flatMap(providers -> service.getBestPrice(product.getId(), providers))
                .block();

        var bestPrice = providerService.getRelevantProducts(product.getId())
                .map(providers -> providers.stream()
                        .map(dto -> ProductProviderDto.builder()
                                .price(dto.getPrice() - 50d)
                                .providerId(dto.getProviderId())
                                .build())
                        .toList())
                .flatMap(providers -> service.getBestPrice(product.getId(), providers))
                .block();

        Assertions.assertNotNull(bestPrice);
        Assertions.assertEquals(50d, bestPrice.getPrice());
        Assertions.assertEquals(50d, bestPrice.getPercent());
        Assertions.assertTrue(bestPrice.getHot());
    }
}
