package com.alco.searchapp;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.dto.RatingDto;
import com.alco.searchapp.provider.dto.Product;
import com.alco.searchapp.provider.dto.ProductInfo;
import com.alco.searchapp.service.ProductService;
import com.alco.searchapp.service.RatingService;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;

abstract public class BaseFunctionalTest extends BaseTest {
    protected static String EAN1 = "0000000000001";
    protected static String EAN2 = "0000000000002";
    protected static String EAN3 = "0000000000003";
    protected static String EAN4 = "0000000000004";
    protected static String EAN5 = "0000000000005";
    protected static String EAN6 = "0000000000006";

    @Autowired
    protected ProductService productService;

    @Autowired
    protected RatingService ratingService;

    @Autowired
    protected R2dbcEntityTemplate template;

    @BeforeEach
    public void setup() {
        cleanDatabase();
    }

    protected void cleanDatabase() {
        template.getDatabaseClient().sql(getResourceContent("db/clean-db.sql")).fetch().rowsUpdated().block();
    }

    protected void addProducts() {
        Flux.just(
                create(EAN1, ProviderTypeEnum.ALCOMAG, CategoryTypeEnum.RUM, 100d, "Scotland", 40f, 5f, 10),
                create(EAN1, ProviderTypeEnum.MAUDAU, CategoryTypeEnum.RUM, 110d, "Scotland", 40f, 4.2f, 50),
                create(EAN1, ProviderTypeEnum.ROZETKA, CategoryTypeEnum.RUM, 120d, "Scotland", 40f, 3f, 100),

                create(EAN2, ProviderTypeEnum.ALCOMAG, CategoryTypeEnum.ABSENT, 200d, "Scotland", 40f, 2f, 10),
                create(EAN2, ProviderTypeEnum.MAUDAU, CategoryTypeEnum.ABSENT, 210d, "Scotland", 40f, 2.5f , 50),
                create(EAN2, ProviderTypeEnum.ROZETKA, CategoryTypeEnum.ABSENT, 220d, "Scotland", 40f, 3f, 100),

                create(EAN3, ProviderTypeEnum.ALCOMAG, CategoryTypeEnum.COGNAC, 300d, "USA", 40f),
                create(EAN3, ProviderTypeEnum.MAUDAU, CategoryTypeEnum.COGNAC, 310d, "USA", 40f),
                create(EAN3, ProviderTypeEnum.ROZETKA, CategoryTypeEnum.COGNAC, 320d, "USA", 40f),

                create(EAN4, ProviderTypeEnum.ALCOMAG, CategoryTypeEnum.RUM, 400d, "Cuba", 40f),
                create(EAN4, ProviderTypeEnum.MAUDAU, CategoryTypeEnum.RUM, 410d, "Cuba", 40f),
                create(EAN4, ProviderTypeEnum.ROZETKA, CategoryTypeEnum.RUM, 420d, "Cuba", 40f),

                create(EAN5, ProviderTypeEnum.ALCOMAG, CategoryTypeEnum.COGNAC, 500d, "Brazil", 35f),
                create(EAN5, ProviderTypeEnum.MAUDAU, CategoryTypeEnum.COGNAC, 510d, "Brazil", 35f),
                create(EAN5, ProviderTypeEnum.ROZETKA, CategoryTypeEnum.COGNAC, 520d, "Brazil", 35f),

                create(EAN6, ProviderTypeEnum.ALCOMAG, CategoryTypeEnum.LIQUEUR, 600d, "Ukraine", 20f),
                create(EAN6, ProviderTypeEnum.MAUDAU, CategoryTypeEnum.LIQUEUR, 610d, "Ukraine", 20f),
                create(EAN6, ProviderTypeEnum.ROZETKA, CategoryTypeEnum.LIQUEUR, 620d, "Ukraine", 20f),
                create(EAN6, ProviderTypeEnum.OKWINE, CategoryTypeEnum.LIQUEUR, 630d, "Ukraine", 20f))
            .flatMap(product -> productService.addProduct(product)
                    .filter(providerProduct -> product.getInfo().getRating() != null)
                    .flatMap(providerProduct -> ratingService.addRating(providerProduct.getProductId(),
                            providerProduct.getProviderId(),
                            new RatingDto(product.getInfo().getRating(), product.getInfo().getVotes()))))
            .collectList()
            .block();
    }
    protected Product create(String ean, ProviderTypeEnum provider, CategoryTypeEnum category, double price,
                                 String country, float abv) {
        return create(ean, provider, category, price, country, abv, null, null);
    }

    protected Product create(String ean, ProviderTypeEnum provider, CategoryTypeEnum category, Double price,
                             String country, Float abv, Float rating, Integer votes) {
        return Product.builder()
                .category(category)
                .providerType(provider)
                .url("")
                .info(ProductInfo.builder()
                        .ean(ean)
                        .name("Name for " + ean)
                        .imageUrls(List.of())
                        .price(price)
                        .volume(700)
                        .abv(abv)
                        .country(country)
                        .brand("Some brand")
                        .rating(rating)
                        .votes(votes)
                        .build()
                )
                .build();
    }

    protected void assertContains(String content, String value) {
        Assertions.assertTrue(content.contains(value),
                String.format("The content (%s) doesn't contain value (%s)", content, value));
    }

    protected void assertNotContains(String content, String value) {
        Assertions.assertFalse(content.contains(value),
                String.format("The content (%s) contains value (%s)", content, value));
    }
}
