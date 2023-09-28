package com.alco.searchapp.service;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.CharacteristicTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.repository.CategoryRepository;
import com.alco.searchapp.repository.CharacteristicRepository;
import com.alco.searchapp.repository.ProviderRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest()
public class ProductListServiceTest extends BaseFunctionalTest {
    @Autowired
    private ProductSearchService productSearchService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CharacteristicRepository characteristicRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @BeforeEach
    public void setup() {
        cleanDatabase();
        addProducts();
        productSearchService.refresh().block();
    }

    @Test
    public void getProductsAll() {
        var filter = getFilterableBuilder()
                .build();
        var list = productSearchService.getProducts(filter, PageRequest.of(0, 6))
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(6, list.size());
    }

    @Test
    public void getProductsForCategories() {
        var rumCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.RUM.getId())
                .blockOptional().orElseThrow();
        var cognacCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.COGNAC.getId())
                .blockOptional().orElseThrow();

        var filter = getFilterableBuilder()
                .categories(List.of(rumCategory.getId(), cognacCategory.getId()))
                .build();

        var list = productSearchService.getProducts(filter, PageRequest.of(0, 6))
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(4, list.size());
    }

    @Test
    public void getProductsForCategoriesWithEan() {
        var rumCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.RUM.getId())
                .blockOptional().orElseThrow();
        var cognacCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.COGNAC.getId())
                .blockOptional().orElseThrow();

        var filter = getFilterableBuilder()
                .categories(List.of(rumCategory.getId(), cognacCategory.getId()))
                .search(EAN4)
                .build();

        var list = productSearchService.getProducts(filter, PageRequest.of(0, 6))
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    public void getProductsForCategoriesWithName() {
        var rumCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.RUM.getId())
                .blockOptional().orElseThrow();
        var cognacCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.COGNAC.getId())
                .blockOptional().orElseThrow();

        var filter = getFilterableBuilder()
                .categories(List.of(rumCategory.getId(), cognacCategory.getId()))
                .search("Name for " + EAN4)
                .build();

        var pageable = PageRequest.of(1, 10);

        var list = productSearchService.getProducts(filter, PageRequest.of(0, 6))
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    public void getProductsForCategoriesWithNameLowerCase() {
        var rumCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.RUM.getId())
                .blockOptional().orElseThrow();
        var cognacCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.COGNAC.getId())
                .blockOptional().orElseThrow();

        var filter = getFilterableBuilder()
                .categories(List.of(rumCategory.getId(), cognacCategory.getId()))
                .search("NAME FOR " + EAN4)
                .build();

        var list = productSearchService.getProducts(filter, PageRequest.of(0, 6))
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    public void getProductsForCategoriesWithNotExistName() {
        var rumCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.RUM.getId())
                .blockOptional().orElseThrow();
        var cognacCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.COGNAC.getId())
                .blockOptional().orElseThrow();

        var filter = getFilterableBuilder()
                .categories(List.of(rumCategory.getId(), cognacCategory.getId()))
                .search("Name for " + EAN2)
                .build();

        var list = productSearchService.getProducts(filter, PageRequest.of(0, 6))
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(0, list.size());
    }

    @Test
    public void getProductsForCharacteristics() {
        var usa = characteristicRepository.findOneByTypeIdAndValue(
                CharacteristicTypeEnum.COUNTRY.getId(), "USA").block();
        Assertions.assertNotNull(usa);
        var abv40 = characteristicRepository.findOneByTypeIdAndValue(
                CharacteristicTypeEnum.ALCOHOL_BY_VOLUME.getId(), "40.0").block();
        Assertions.assertNotNull(abv40);

        var filter = getFilterableBuilder()
                .characteristics(List.of(usa.getId(), abv40.getId()))
                .build();

        var list = productSearchService.getProducts(filter, PageRequest.of(0, 6))
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    public void getProductsForProviders() {
        var entity = providerRepository.findOneByTypeId(ProviderTypeEnum.OKWINE.getId()).block();
        Assertions.assertNotNull(entity);

        var filter = getFilterableBuilder()
                .providers(List.of(entity.getId()))
                .build();

        var list = productSearchService.getProducts(filter, PageRequest.of(0, 6))
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, list.size());

    }

    @Test
    public void getProductsForPriceRange() {
        var filter = getFilterableBuilder()
                .price(new Filterable.Price(200d, 400d))
                .build();

        var list = productSearchService.getProducts(filter, PageRequest.of(0, 6))
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(3, list.size());
    }

    @Test
    public void getProductsForPriceRangeWithProviders() {
        var entity = providerRepository.findOneByTypeId(ProviderTypeEnum.MAUDAU.getId()).block();
        Assertions.assertNotNull(entity);

        var filter = getFilterableBuilder()
                .providers(List.of(entity.getId()))
                .price(new Filterable.Price(305d, 315d))
                .build();

        var list = productSearchService.getProducts(filter, PageRequest.of(0, 6))
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    public void getProductsForPriceRangeWithProvidersWithoutMax() {
        var entity = providerRepository.findOneByTypeId(ProviderTypeEnum.MAUDAU.getId()).block();
        Assertions.assertNotNull(entity);

        var filter = getFilterableBuilder()
                .providers(List.of(entity.getId()))
                .price(new Filterable.Price(305d, null))
                .build();

        var list = productSearchService.getProducts(filter, PageRequest.of(0, 6))
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(4, list.size());
    }

    @Test
    public void getProductsForPriceRangeWithProvidersWithoutMin() {
        var entity = providerRepository.findOneByTypeId(ProviderTypeEnum.MAUDAU.getId()).block();
        Assertions.assertNotNull(entity);

        var filter = getFilterableBuilder()
                .providers(List.of(entity.getId()))
                .price(new Filterable.Price(null, 315d))
                .build();

        var list = productSearchService.getProducts(filter, PageRequest.of(0, 6))
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(3, list.size());
    }

    private Filterable.FilterableBuilder getFilterableBuilder() {
        return Filterable.builder()
                .categories(List.of())
                .providers(List.of())
                .search(null)
                .characteristics(List.of())
                .price(new Filterable.Price(null, null));
    }
}
