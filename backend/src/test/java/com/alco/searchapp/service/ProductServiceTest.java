package com.alco.searchapp.service;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.CharacteristicTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.dto.CharacteristicDto;
import com.alco.searchapp.provider.dto.Product;
import com.alco.searchapp.provider.dto.ProductInfo;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest()
public class ProductServiceTest extends BaseFunctionalTest {
    @Test
    public void addProduct() {
        var product = Product.builder()
                .category(CategoryTypeEnum.RUM)
                .providerType(ProviderTypeEnum.ALCOMAG)
                .info(ProductInfo.builder()
                        .name("Some name")
                        .ean("00001")
                        .description("Some description")
                        .price(100d)
                        .brand("Some brand")
                        .abv(43.5f)
                        .volume(700)
                        .country("Ukraine")
                        .imageUrls(List.of("https://alcomag.ua/upload/iblock/e6a/nknlk1kpzqj57jhqjbg9b7dw6bmefr80.jpeg"))
                        .build())
                .build();

        var dto = productService.addProduct(product)
                .flatMap(entity -> productService.getProduct(entity.getProductId()))
                .blockOptional()
                .orElseThrow();

        Assertions.assertNotNull(dto.getId());
        Assertions.assertEquals(product.getInfo().getName(), dto.getName());
        Assertions.assertEquals(product.getInfo().getEan(), dto.getEan());
        Assertions.assertEquals(product.getInfo().getDescription(), dto.getDescription());
        Assertions.assertEquals(product.getCategory(), CategoryTypeEnum.RUM);
        Assertions.assertEquals(product.getInfo().getPrice(), dto.getProviders().get(0).getPrice());
        Assertions.assertEquals(product.getProviderType(), dto.getProviders().get(0).getProviderType());
        Assertions.assertFalse(dto.getImageUrls().isEmpty());
        Assertions.assertTrue(dto.getImageUrls().get(0).contains(dto.getId().toString()));
        Assertions.assertEquals(product.getInfo().getAbv(), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.ALCOHOL_BY_VOLUME))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
        Assertions.assertEquals(product.getInfo().getCountry(), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.COUNTRY))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
        Assertions.assertEquals(product.getInfo().getVolume(), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.VOLUME))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
        Assertions.assertEquals(String.valueOf(product.getInfo().getBrand()), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.BRAND))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
    }

    @Test
    public void addProductInfoWithoutAbv() {
        var product = Product.builder()
                .category(CategoryTypeEnum.RUM)
                .providerType(ProviderTypeEnum.ALCOMAG)
                .info(ProductInfo.builder()
                        .name("Some name")
                        .ean("00001")
                        .description("Some description")
                        .price(100d)
                        .brand("Some brand")
                        .volume(700)
                        .country("Ukraine")
                        .imageUrls(List.of())
                        .build())
                .build();

        var dto = productService.addProduct(product)
                .flatMap(entity -> productService.getProduct(entity.getProductId()))
                .blockOptional()
                .orElseThrow();

        Assertions.assertNotNull(dto.getId());
        Assertions.assertEquals(product.getInfo().getName(), dto.getName());
        Assertions.assertEquals(product.getInfo().getEan(), dto.getEan());
        Assertions.assertEquals(product.getInfo().getDescription(), dto.getDescription());
        Assertions.assertEquals(product.getCategory(), CategoryTypeEnum.RUM);
        Assertions.assertEquals(product.getInfo().getPrice(), dto.getProviders().get(0).getPrice());
        Assertions.assertEquals(product.getProviderType(), dto.getProviders().get(0).getProviderType());
        Assertions.assertEquals(product.getInfo().getImageUrls(), dto.getImageUrls());
        Assertions.assertNull(dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.ALCOHOL_BY_VOLUME))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
        Assertions.assertEquals(product.getInfo().getCountry(), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.COUNTRY))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
        Assertions.assertEquals(product.getInfo().getVolume(), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.VOLUME))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
        Assertions.assertEquals(String.valueOf(product.getInfo().getBrand()), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.BRAND))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
    }

    @Test
    public void addProductInfoWithoutDescription() {
        var productWithoutDescription = Product.builder()
                .category(CategoryTypeEnum.RUM)
                .providerType(ProviderTypeEnum.ALCOMAG)
                .info(ProductInfo.builder()
                        .name("Some name")
                        .ean("00001")
                        .price(100d)
                        .brand("Some brand")
                        .volume(700)
                        .country("Ukraine")
                        .imageUrls(List.of())
                        .build())
                .build();

        productService.addProduct(productWithoutDescription)
                .blockOptional()
                .orElseThrow();

        var productWithDescription = Product.builder()
                .category(CategoryTypeEnum.RUM)
                .providerType(ProviderTypeEnum.ALCOMAG)
                .info(ProductInfo.builder()
                        .name("Some name 2")
                        .ean("00001")
                        .description("Some description")
                        .price(100d)
                        .brand("Some brand")
                        .volume(700)
                        .country("Ukraine")
                        .imageUrls(List.of())
                        .build())
                .build();

        var dto = productService.addProduct(productWithDescription)
                .flatMap(entity -> productService.getProduct(entity.getProductId()))
                .blockOptional()
                .orElseThrow();

        Assertions.assertNotNull(dto.getId());
        Assertions.assertEquals(productWithoutDescription.getInfo().getName(), dto.getName());
        Assertions.assertEquals(productWithoutDescription.getInfo().getEan(), dto.getEan());
        Assertions.assertEquals(productWithDescription.getInfo().getDescription(), dto.getDescription());
        Assertions.assertEquals(productWithoutDescription.getCategory(), CategoryTypeEnum.RUM);
        Assertions.assertEquals(productWithoutDescription.getInfo().getPrice(), dto.getProviders().get(0).getPrice());
        Assertions.assertEquals(productWithoutDescription.getProviderType(), dto.getProviders().get(0).getProviderType());
        Assertions.assertEquals(productWithoutDescription.getInfo().getImageUrls(), dto.getImageUrls());
        Assertions.assertNull(dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.ALCOHOL_BY_VOLUME))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
        Assertions.assertEquals(productWithoutDescription.getInfo().getCountry(), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.COUNTRY))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
        Assertions.assertEquals(productWithoutDescription.getInfo().getVolume(), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.VOLUME))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
        Assertions.assertEquals(String.valueOf(productWithoutDescription.getInfo().getBrand()), dto.getCharacteristics().stream()
                .filter(characteristicDto -> characteristicDto.getType().equals(CharacteristicTypeEnum.BRAND))
                .map(CharacteristicDto::getValue)
                .findAny().orElse(null));
    }
}
