package com.alco.searchapp.service;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.CharacteristicTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.dto.CharacteristicDto;
import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.provider.dto.Product;
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
public class ProductServiceTest extends BaseFunctionalTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CharacteristicRepository characteristicRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private ProviderProductRepository providerProductRepository;

    @Test
    public void addProductInfo() {
        var product = Product.builder()
                .name("Some name")
                .ean("00001")
                .description("Some description")
                .category(CategoryTypeEnum.RUM)
                .price(100d)
                .providerType(ProviderTypeEnum.ALCOMAG)
                .brand("Some brand")
                .abv(43.5f)
                .volume(700)
                .country("Ukraine")
                .imageUrls(List.of())
                .build();

        var dto = productService.addProductInfo(product)
                .flatMap(entity -> productService.getProduct(entity.getProductId()))
                .blockOptional()
                .orElseThrow();

        Assertions.assertNotNull(dto.getId());
        Assertions.assertEquals(product.getName(), dto.getName());
        Assertions.assertEquals(product.getEan(), dto.getEan());
        Assertions.assertEquals(product.getDescription(), dto.getDescription());
        Assertions.assertEquals(product.getCategory(), CategoryTypeEnum.RUM);
        Assertions.assertEquals(product.getPrice(), dto.getProviders().get(0).getPrice());
        Assertions.assertEquals(product.getProviderType(), dto.getProviders().get(0).getProviderType());
        Assertions.assertEquals(product.getImageUrls(), dto.getImageUrls());
        Assertions.assertEquals(product.getAbv(), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.ALCOHOL_BY_VOLUME))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
        Assertions.assertEquals(product.getCountry(), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.COUNTRY))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
        Assertions.assertEquals(product.getVolume(), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.VOLUME))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
        Assertions.assertEquals(String.valueOf(product.getBrand()), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.BRAND))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
    }

    @Test
    public void addProductInfoWithoutAbv() {
        var product = Product.builder()
                .name("Some name")
                .ean("00001")
                .description("Some description")
                .category(CategoryTypeEnum.RUM)
                .price(100d)
                .providerType(ProviderTypeEnum.ALCOMAG)
                .brand("Some brand")
                .volume(700)
                .country("Ukraine")
                .imageUrls(List.of())
                .build();

        var dto = productService.addProductInfo(product)
                .flatMap(entity -> productService.getProduct(entity.getProductId()))
                .blockOptional()
                .orElseThrow();

        Assertions.assertNotNull(dto.getId());
        Assertions.assertEquals(product.getName(), dto.getName());
        Assertions.assertEquals(product.getEan(), dto.getEan());
        Assertions.assertEquals(product.getDescription(), dto.getDescription());
        Assertions.assertEquals(product.getCategory(), CategoryTypeEnum.RUM);
        Assertions.assertEquals(product.getPrice(), dto.getProviders().get(0).getPrice());
        Assertions.assertEquals(product.getProviderType(), dto.getProviders().get(0).getProviderType());
        Assertions.assertEquals(product.getImageUrls(), dto.getImageUrls());
        Assertions.assertNull(dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.ALCOHOL_BY_VOLUME))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
        Assertions.assertEquals(product.getCountry(), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.COUNTRY))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
        Assertions.assertEquals(product.getVolume(), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.VOLUME))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
        Assertions.assertEquals(String.valueOf(product.getBrand()), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.BRAND))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
    }


    @Test
    public void getProductsAll() {
        addProducts();

        var filter = Filterable.builder().build();

        var pageable = PageRequest.of(1, 10);

        var list = productService.getProducts(filter, pageable)
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
                .build();

        var pageable = PageRequest.of(1, 10);

        var list = productService.getProducts(filter, pageable)
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

        var pageable = PageRequest.of(1, 10);

        var list = productService.getProducts(filter, pageable)
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

        var list = productService.getProducts(filter, pageable)
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

        var pageable = PageRequest.of(1, 10);

        var list = productService.getProducts(filter, pageable)
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

        var pageable = PageRequest.of(1, 10);

        var list = productService.getProducts(filter, pageable)
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

        var pageable = PageRequest.of(1, 10);

        var list = productService.getProducts(filter, pageable)
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

        var pageable = PageRequest.of(1, 10);

        var list = productService.getProducts(filter, pageable)
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

        var pageable = PageRequest.of(1, 10);

        var list = productService.getProducts(filter, pageable)
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(2, list.size());
    }

    @Test
    public void getProductsWithPageable() {
        addProducts();

        var filter = Filterable.builder().build();

        var pageable = PageRequest.of(1, 2);

        var list = productService.getProducts(filter, pageable)
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(2, list.size());
    }

    @Test
    public void getProductsWithPageableLastPage() {
        addProducts();

        var filter = Filterable.builder().build();

        var pageable = PageRequest.of(2, 5);

        var list = productService.getProducts(filter, pageable)
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, list.size());
    }

    @Test
    public void getProductCountAll() {
        addProducts();

        var filter = Filterable.builder().build();

        var count = productService.getProductCount(filter)
                .blockOptional().orElseThrow();
        Assertions.assertEquals(6, count);
    }

    @Test
    public void getProductCountForCategories() {
        addProducts();

        var rumCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.RUM.getId())
                .blockOptional().orElseThrow();
        var cognacCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.COGNAC.getId())
                .blockOptional().orElseThrow();

        var filter = Filterable.builder()
                .categories(List.of(rumCategory.getId(), cognacCategory.getId()))
                .build();

        var count = productService.getProductCount(filter)
                .blockOptional().orElseThrow();
        Assertions.assertEquals(4, count);
    }

    @Test
    public void getProductCountForCategoriesWithEan() {
        addProducts();

        var rumCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.RUM.getId())
                .blockOptional().orElseThrow();
        var cognacCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.COGNAC.getId())
                .blockOptional().orElseThrow();

        var filter = Filterable.builder()
                .categories(List.of(rumCategory.getId(), cognacCategory.getId()))
                .search(EAN4)
                .build();

        var count = productService.getProductCount(filter)
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, count);
    }

    @Test
    public void getProductCountForCategoriesWithName() {
        addProducts();

        var rumCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.RUM.getId())
                .blockOptional().orElseThrow();
        var cognacCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.COGNAC.getId())
                .blockOptional().orElseThrow();

        var filter = Filterable.builder()
                .categories(List.of(rumCategory.getId(), cognacCategory.getId()))
                .search("Name for " + EAN4)
                .build();

        var count = productService.getProductCount(filter)
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, count);
    }

    @Test
    public void getProductCountForCategoriesWithNotExistName() {
        addProducts();

        var rumCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.RUM.getId())
                .blockOptional().orElseThrow();
        var cognacCategory = categoryRepository.findOneByTypeId(CategoryTypeEnum.COGNAC.getId())
                .blockOptional().orElseThrow();

        var filter = Filterable.builder()
                .categories(List.of(rumCategory.getId(), cognacCategory.getId()))
                .search("Name for " + EAN2)
                .build();

        var count = productService.getProductCount(filter)
                .blockOptional().orElseThrow();
        Assertions.assertEquals(0, count);
    }

    @Test
    public void getProductCountForCharacteristics() {
        addProducts();
        var entity = characteristicRepository.findOneByTypeIdAndValue(
                CharacteristicTypeEnum.COUNTRY.getId(), "Ukraine").block();
        Assertions.assertNotNull(entity);

        var filter = Filterable.builder()
                .characteristics(List.of(entity.getId()))
                .build();

        var count = productService.getProductCount(filter)
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, count);
    }

    @Test
    public void getProductCountForProviders() {
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

        var pageable = PageRequest.of(1, 10);

        var list = productService.getProducts(filter, pageable)
                .collectList()
                .blockOptional().orElseThrow();
        Assertions.assertEquals(1, list.size());

    }

    @Test
    public void getProductCountForPriceRange() {
        addProducts();

        var filter = Filterable.builder()
                .price(new Filterable.Price(200d, 400d))
                .build();

        var count = productService.getProductCount(filter)
                .blockOptional().orElseThrow();
        Assertions.assertEquals(2, count);
    }
}
