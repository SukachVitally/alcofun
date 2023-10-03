package com.alco.searchapp.service;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.CharacteristicTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.dto.CharacteristicDto;
import com.alco.searchapp.provider.dto.Product;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest()
public class ProductServiceTest extends BaseFunctionalTest {
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
}
