package com.alco.searchapp.provider;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.properites.provider.OkWineProperties;
import com.alco.searchapp.properites.ProviderBlock;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OkWineProviderTest extends AbstractProviderTest {
    @Test
    public void getPathLinks() {
        var productPageDto = getProvider().getProductPages().blockFirst();
        Assertions.assertNotNull(productPageDto);
        Assertions.assertNotNull(productPageDto.getCategory());
        Assertions.assertEquals(ProviderTypeEnum.OKWINE, productPageDto.getProvider());
    }

    @Test
    public void parsePageInfo() {
        var link = "https://okwine.ua/ua/product/rom-dead-mans-fingers-dmf-black-375-07l?urlProduct=rom-dead-mans-fingers-dmf-black-375-07l";
        var product = getProvider().parsePageInfo(link).block();

        Assertions.assertNotNull(product);
        Assertions.assertEquals("Ром Дед Менс Фінгерс (ДМФ), Блек / Dead Man’s Fingers (DMF), Black, 37.5%, 0.7л", product.getName());
        Assertions.assertEquals("5011166067054", product.getEan());
        Assertions.assertNotNull(product.getPrice());
        Assertions.assertNotNull(product.getDescription());
        Assertions.assertEquals(700, product.getVolume());
        Assertions.assertEquals("Dead Man’s Fingers", product.getBrand());
        Assertions.assertEquals(37.5f, product.getAbv());
        Assertions.assertEquals("Кариби", product.getCountry());
        Assertions.assertEquals(2, product.getImageUrls().size());
    }

    @Test
    public void parsePageInfoWithoutCustomInfo() {
        var link = "https://okwine.ua/ua/product/vodka-klassicheskaya-rada-classic-rada-40-0-7l";
        var product = getProvider().parsePageInfo(link).block();

        Assertions.assertNotNull(product);
        Assertions.assertEquals("4820080723066", product.getEan());
        Assertions.assertFalse(product.getDescription().contains("УТП010212"));
    }

    @Test
    public void parseNonExistPage() {
        var link = "https://okwine1.ua/ua/product/rom-dead-mans-fingers-dmf-black-375-07l?urlProduct=rom-dead-mans-fingers-dmf-black-375-07l";
        var product = getProvider().parsePageInfo(link).block();

        Assertions.assertNotNull(product);
        Assertions.assertNull(product.getEan());
    }

    private OkWineProvider getProvider() {
        return new OkWineProvider(objectMapper(), linkParser(), getProperties());
    }

    private OkWineProperties getProperties() {
        var block = new ProviderBlock();
        block.setType(CategoryTypeEnum.ABSENT);
        block.setUrl("https://okwine.ua/api/v1/filter/products?14=826&city=61e159f3ab2700007200435f");
        var properties = new OkWineProperties();
        properties.setBlocks(List.of(block));
        return properties;
    }
}
