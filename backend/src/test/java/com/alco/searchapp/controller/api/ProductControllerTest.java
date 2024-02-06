package com.alco.searchapp.controller.api;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.CharacteristicTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.repository.CategoryRepository;
import com.alco.searchapp.repository.CharacteristicRepository;
import com.alco.searchapp.repository.ProductRepository;
import com.alco.searchapp.repository.ProviderRepository;
import com.alco.searchapp.repository.elastic.ProductSearchRepository;
import com.alco.searchapp.service.ElasticCollectService;
import com.alco.searchapp.service.ProductSearchService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest()
@AutoConfigureWebTestClient
public class ProductControllerTest extends BaseFunctionalTest {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private CharacteristicRepository characteristicRepository;

    @Autowired
    private ElasticCollectService elasticService;

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @BeforeEach
    public void setup() {
        cleanDatabase();
        productSearchRepository.deleteAll().block();
        addProducts();
        elasticService.refresh().block();
    }

    @Test
    void testGetAllProductList() {
        webClient.get()
                .uri("/api/product/")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.results.length()").isEqualTo(6)
                .jsonPath("$.pageCount").isEqualTo(1)
                .jsonPath("$.currentPage").isEqualTo(1);
    }

    @Test
    void testGetProductListForCategory() {
        var category = categoryRepository.findOneByTypeId(CategoryTypeEnum.RUM.getId()).block();

        Assertions.assertNotNull(category);
        webClient.get()
                .uri("/api/product/?categories=" + category.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.results.length()").isEqualTo(2);
    }

    @Test
    void testGetProductListForProvider() {
        var provider = providerRepository.findOneByTypeId(ProviderTypeEnum.ALCOMAG.getId()).block();

        Assertions.assertNotNull(provider);
        webClient.get()
                .uri("/api/product/?providers=" + provider.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.results.length()").isEqualTo(6);
    }

    @Test
    void testGetProductListForCharacteristics() {
        var characteristic = characteristicRepository.findOneByTypeIdAndValue(
                CharacteristicTypeEnum.VOLUME.getId(), "700")
                .block();

        Assertions.assertNotNull(characteristic);
        webClient.get()
                .uri("/api/product/?characteristics=" + characteristic.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.results.length()").isEqualTo(6);
    }

    @Test
    void testGetProductListForPriceRange() {
        webClient.get()
                .uri("/api/product/?price_min=500&price_max=600")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.results.length()").isEqualTo(2);
    }

    @Test
    void testGetAllProductListWithPagination() {
        webClient.get()
                .uri("/api/product/?page=2&size=4")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.results.length()").isEqualTo(2)
                .jsonPath("$.pageCount").isEqualTo(2)
                .jsonPath("$.currentPage").isEqualTo(2);
    }

    @Test
    void testGetAllProductListWithSortByName() {
        webClient.get()
                .uri("/api/product/?sort=name,desc")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.results.length()").isEqualTo(6)
                .jsonPath("$.pageCount").isEqualTo(1)
                .jsonPath("$.currentPage").isEqualTo(1)
                .jsonPath("$.results[0].name").isEqualTo("Name for " + EAN6);
    }

    @Test
    void testGetProduct() {
        var product = repository.findAll().take(1).single().block();
        Assertions.assertNotNull(product);
        webClient.get()
                .uri("/api/product/" + product.getId())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetProductNotFound() {
        webClient.get()
                .uri("/api/product/" + 10000000)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Product with id '10000000' not found");
    }

    @Test
    void testGetProductPriceRange() {
        webClient.get()
                .uri("/api/product/price-range")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.min").isEqualTo(100)
                .jsonPath("$.max").isEqualTo(600);
    }
}
