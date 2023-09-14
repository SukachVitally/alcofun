package com.alco.searchapp;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.provider.dto.Product;
import com.alco.searchapp.service.ProductService;
import java.util.List;
import java.util.stream.Stream;
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
    protected R2dbcEntityTemplate template;

    @BeforeEach
    public void setup() {
        cleanDatabase();
    }

    protected void cleanDatabase() {
        template.getDatabaseClient().sql(getResourceContent("db/clean-db.sql")).fetch().rowsUpdated().block();
    }

    protected void addProducts() {
        Flux.just(createBlock(EAN1, CategoryTypeEnum.RUM, 100d, "Scotland"),
                        createBlock(EAN2, CategoryTypeEnum.ABSENT, 200d, "Poland"),
                        createBlock(EAN3, CategoryTypeEnum.COGNAC, 300d, "USA"),
                        createBlock(EAN4, CategoryTypeEnum.RUM, 400d, "Brazil"),
                        createBlock(EAN5, CategoryTypeEnum.COGNAC, 500d, "Cuba"),
                        createBlock(EAN6, CategoryTypeEnum.LIQUEUR, 600d, "Ukraine"))
                .flatMap(Flux::fromIterable)
                .flatMap(productService::addProductInfo)
                .collectList()
                .block();
    }

    private List<Product> createBlock(String ean, CategoryTypeEnum category, double price, String country) {
        return Stream.of(ProviderTypeEnum.values())
                .map(provider -> create(ean, provider, category, price + provider.ordinal() * 10d, country))
                .toList();
    }

    private Product create(String ean, ProviderTypeEnum provider, CategoryTypeEnum category, double price, String country) {
        return Product.builder()
                .ean(ean)
                .name("Name for " + ean)
                .category(category)
                .providerType(provider)
                .link("")
                .imageUrls(List.of())
                .price(price)
                .volume(700)
                .abv(30f)
                .country(country)
                .brand("Some brand")
                .build();
    }
}
