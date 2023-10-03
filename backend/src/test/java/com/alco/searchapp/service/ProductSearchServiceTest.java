package com.alco.searchapp.service;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.CharacteristicTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.dto.productlist.ProductInfoDto;
import com.alco.searchapp.repository.CategoryRepository;
import com.alco.searchapp.repository.CharacteristicRepository;
import com.alco.searchapp.repository.ProviderRepository;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SpringBootTest()
public class ProductSearchServiceTest extends BaseFunctionalTest {
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
        var res = productSearchService.getProducts(filter, PageRequest.of(1, 6))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(6, res.getResults().size());
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

        var res = productSearchService.getProducts(filter, PageRequest.of(1, 6))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(4, res.getResults().size());
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

        var res = productSearchService.getProducts(filter, PageRequest.of(1, 6))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, res.getResults().size());
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

        var res = productSearchService.getProducts(filter, PageRequest.of(1, 6))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, res.getResults().size());
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

        var res = productSearchService.getProducts(filter, PageRequest.of(1, 6))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, res.getResults().size());
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

        var res = productSearchService.getProducts(filter, PageRequest.of(1, 6))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(0, res.getResults().size());
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

        var res = productSearchService.getProducts(filter, PageRequest.of(1, 6))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, res.getResults().size());
    }

    @Test
    public void getProductsForProviders() {
        var entity = providerRepository.findOneByTypeId(ProviderTypeEnum.OKWINE.getId()).block();
        Assertions.assertNotNull(entity);

        var filter = getFilterableBuilder()
                .providers(List.of(entity.getId()))
                .build();

        var res = productSearchService.getProducts(filter, PageRequest.of(1, 6))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, res.getResults().size());
    }

    @Test
    public void getProductsForPriceRange() {
        var filter = getFilterableBuilder()
                .price(new Filterable.Price(200d, 400d))
                .build();

        var res = productSearchService.getProducts(filter, PageRequest.of(1, 6))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(3, res.getResults().size());
    }

    @Test
    public void getProductsForPriceRangeWithProviders() {
        var entity = providerRepository.findOneByTypeId(ProviderTypeEnum.MAUDAU.getId()).block();
        Assertions.assertNotNull(entity);

        var filter = getFilterableBuilder()
                .providers(List.of(entity.getId()))
                .price(new Filterable.Price(305d, 315d))
                .build();

        var res = productSearchService.getProducts(filter, PageRequest.of(1, 6))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, res.getResults().size());
    }

    @Test
    public void getProductsForPriceRangeWithProvidersWithoutMax() {
        var entity = providerRepository.findOneByTypeId(ProviderTypeEnum.MAUDAU.getId()).block();
        Assertions.assertNotNull(entity);

        var filter = getFilterableBuilder()
                .providers(List.of(entity.getId()))
                .price(new Filterable.Price(305d, null))
                .build();

        var res = productSearchService.getProducts(filter, PageRequest.of(1, 6))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(4, res.getResults().size());
    }

    @Test
    public void getProductsForPriceRangeWithProvidersWithoutMin() {
        var entity = providerRepository.findOneByTypeId(ProviderTypeEnum.MAUDAU.getId()).block();
        Assertions.assertNotNull(entity);

        var filter = getFilterableBuilder()
                .providers(List.of(entity.getId()))
                .price(new Filterable.Price(null, 315d))
                .build();

        var res = productSearchService.getProducts(filter, PageRequest.of(1, 6))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(3, res.getResults().size());
    }

    @Test
    public void getProductsAllWithPageOffset() {
        var filter = getFilterableBuilder()
                .build();
        var res = productSearchService.getProducts(filter, PageRequest.of(2, 4))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(2, res.getResults().size());
    }

    @Test
    public void getProductsAllWithSortByName() {
        var filter = getFilterableBuilder()
                .build();
        var res = productSearchService.getProducts(filter, PageRequest.of(1, 6,
                        Sort.by(Sort.Direction.DESC, "name")))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(6, res.getResults().size());
        Assertions.assertEquals("Name for " + EAN6, res.getResults().get(0).getName());
        Assertions.assertEquals("Name for " + EAN1, res.getResults().get(5).getName());
    }

    @Test
    public void getProductsAllWithSortByCreatedAt() {
        var filter = getFilterableBuilder()
                .build();
        var res = productSearchService.getProducts(filter, PageRequest.of(1, 6,
                        Sort.by(Sort.Direction.DESC, "created_at")))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(6, res.getResults().size());

        Assertions.assertEquals(res.getResults().get(0).getId(), res.getResults().stream()
                .max(Comparator.comparingLong(ProductInfoDto::getId))
                .map(ProductInfoDto::getId).orElse(0L));

        Assertions.assertEquals(res.getResults().get(5).getId(), res.getResults().stream()
                .min(Comparator.comparingLong(ProductInfoDto::getId))
                .map(ProductInfoDto::getId).orElse(0L));
    }

    @Test
    public void getProductsAllWithSortByRatingDesc() {
        var filter = getFilterableBuilder()
                .build();
        var res = productSearchService.getProducts(filter, PageRequest.of(1, 6,
                        Sort.by(Sort.Direction.DESC, "rating")))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(6, res.getResults().size());

        Assertions.assertEquals(res.getResults().get(0).getEan(), EAN1);
        Assertions.assertEquals(res.getResults().get(5).getEan(), EAN2);
    }

    @Test
    public void getProductsAllWithSortByRatingAsc() {
        var filter = getFilterableBuilder()
                .build();
        var res = productSearchService.getProducts(filter, PageRequest.of(1, 6,
                        Sort.by(Sort.Direction.ASC, "rating")))
                .blockOptional().orElseThrow();
        Assertions.assertEquals(6, res.getResults().size());

        Assertions.assertEquals(res.getResults().get(0).getEan(), EAN2);
        Assertions.assertEquals(res.getResults().get(5).getEan(), EAN1);
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
