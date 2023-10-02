package com.alco.searchapp.service;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.dto.RatingDto;
import com.alco.searchapp.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest()
public class RatingServiceTest extends BaseFunctionalTest {
    @Autowired
    private RatingService service;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProviderService providerService;

    @Test
    public void getCompositeRating() {
        addProducts();

        var product = productRepository.findOneByEan(EAN1).block();
        Assertions.assertNotNull(product);

        addRating(product.getId(), 0);

        var compositeRating = service.getCompositeRating(product.getId()).block();
        Assertions.assertNotNull(compositeRating);
        Assertions.assertEquals(2.5f, compositeRating.rating());
        Assertions.assertEquals(40, compositeRating.votes());
    }

    @Test
    public void getInternalRating() {
        addProducts();

        var product = productRepository.findOneByEan(EAN1).block();
        Assertions.assertNotNull(product);

        addRating(product.getId(), 0);

        var compositeRating = service.getInternalRating(product.getId()).block();
        Assertions.assertEquals(1.5f, compositeRating);
    }

    @Test
    public void getInternalRatingStartFrom2Stars() {
        addProducts();

        var product = productRepository.findOneByEan(EAN1).block();
        Assertions.assertNotNull(product);

        addRating(product.getId(), 2);

        var compositeRating = service.getInternalRating(product.getId()).block();
        Assertions.assertEquals(6.3f, compositeRating);
    }

    private void addRating(long productId, float startRating) {
        providerService.getProviders().flatMap(provider -> {
            var rating = new RatingDto(startRating + (float) provider.getType().getId(), 10);
            return service.addRating(productId, provider.getId(), rating);
        }).collectList().block();
    }
}
