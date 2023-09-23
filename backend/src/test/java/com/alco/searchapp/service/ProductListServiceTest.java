package com.alco.searchapp.service;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.CharacteristicTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.repository.CategoryRepository;
import com.alco.searchapp.repository.CharacteristicRepository;
import com.alco.searchapp.repository.ProviderProductRepository;
import com.alco.searchapp.repository.ProviderRepository;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

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

    @Autowired
    private ProviderProductRepository providerProductRepository;

    @Test
    public void getProductsAll() {
        addProducts();
        productSearchService.refresh().block();

        var filter = Filterable
                .builder()
                .categories(List.of())
                .providers(List.of())
                .search(null)
                .characteristics(List.of())
//                .price()
                .build();

        var list = productSearchService.getProducts(filter)
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(6, list.size());
    }

    @Test
    public void getProductsForCategories() {
        addProducts();

        var rumCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.RUM.getId())
                .blockOptional().orElseThrow();
        var cognacCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.COGNAC.getId())
                .blockOptional().orElseThrow();

        var filter = Filterable.builder()
                .categories(List.of(rumCategory.getId(), cognacCategory.getId()))
                .providers(List.of())
                .search(null)
                .characteristics(List.of())
                .build();

        var list = productSearchService.getProducts(filter)
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(4, list.size());
    }

    @Test
    public void getProductsForCategoriesWithEan() {
        addProducts();

        var rumCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.RUM.getId())
                .blockOptional().orElseThrow();
        var cognacCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.COGNAC.getId())
                .blockOptional().orElseThrow();

        var filter = Filterable.builder()
                .categories(List.of(rumCategory.getId(), cognacCategory.getId()))
                .search(EAN4)
                .build();

        var list = productSearchService.getProducts(filter)
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    public void getProductsForCategoriesWithName() {
        addProducts();

        var rumCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.RUM.getId())
                .blockOptional().orElseThrow();
        var cognacCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.COGNAC.getId())
                .blockOptional().orElseThrow();

        var filter = Filterable.builder()
                .categories(List.of(rumCategory.getId(), cognacCategory.getId()))
                .search("Name for " + EAN4)
                .build();

        var pageable = PageRequest.of(1, 10);

        var list = productSearchService.getProducts(filter)
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    public void getProductsForCategoriesWithNameLowerCase() {
        addProducts();

        var rumCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.RUM.getId())
                .blockOptional().orElseThrow();
        var cognacCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.COGNAC.getId())
                .blockOptional().orElseThrow();

        var filter = Filterable.builder()
                .categories(List.of(rumCategory.getId(), cognacCategory.getId()))
                .search("NAME FOR " + EAN4)
                .build();

        var list = productSearchService.getProducts(filter)
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    public void getProductsForCategoriesWithNotExistName() {
        addProducts();

        var rumCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.RUM.getId())
                .blockOptional().orElseThrow();
        var cognacCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.COGNAC.getId())
                .blockOptional().orElseThrow();

        var filter = Filterable.builder()
                .categories(List.of(rumCategory.getId(), cognacCategory.getId()))
                .search("Name for " + EAN2)
                .build();

        var list = productSearchService.getProducts(filter)
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(0, list.size());
    }

    @Test
    public void getProductsForCharacteristics() {
        addProducts();
        var entity = characteristicRepository.findOneByTypeIdAndValue(
                CharacteristicTypeEnum.COUNTRY.getId(), "Ukraine").block();
        Assertions.assertNotNull(entity);

        var filter = Filterable.builder()
                .characteristics(List.of(entity.getId()))
                .build();

        var list = productSearchService.getProducts(filter)
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    public void getProductsForProviders() {
        addProducts();

        var entity = providerRepository.findOneByTypeId(ProviderTypeEnum.ALCOMAG.getId()).block();
        Assertions.assertNotNull(entity);

        providerProductRepository.findAll()
                .filter(providerProductEntity -> providerProductEntity.getProviderId().equals(entity.getId()))
                .skip(1)
                .flatMap(providerProductEntity -> providerProductRepository.delete(providerProductEntity))
                .collectList().block();

        var filter = Filterable.builder()
                .providers(List.of(entity.getId()))
                .build();

        var list = productSearchService.getProducts(filter)
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, list.size());

    }

    @Test
    public void getProductsForPriceRange() {
        addProducts();

        var filter = Filterable.builder()
                .price(new Filterable.Price(200d, 400d))
                .build();

        var list = productSearchService.getProducts(filter)
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(3, list.size());
    }
}
