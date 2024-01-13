package com.alco.searchapp.provider;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.properites.ProviderBlock;
import com.alco.searchapp.properites.provider.FozzyProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FozzyProviderTest extends AbstractProviderTest {
    @Test
    public void getPathLinks() {
        var productPageDto = getProvider().getProductPages().blockFirst();
        Assertions.assertNotNull(productPageDto);
        Assertions.assertEquals(ProviderTypeEnum.FOZZY, productPageDto.getProvider());
        Assertions.assertNotNull(productPageDto.getUrl());
        Assertions.assertNotNull(productPageDto.getCategory());
    }

    @Test
    public void getProductCorrect() {
        var url = "https://fozzyshop.ua/rom/31578-rom-cayo-grande-anekho-8414771850078.html";
        var product = getProvider()
                .parsePageInfo(url)
                .block();

        Assertions.assertNotNull(product);
        Assertions.assertEquals("Ром Cayo Grande Анехо", product.getName());
        Assertions.assertEquals("8414771850078", product.getEan());
        Assertions.assertNotNull(product.getPrice());
        Assertions.assertEquals(1000, product.getVolume());
        Assertions.assertEquals("Cayo Grande Club", product.getBrand());
        Assertions.assertEquals(37.5f, product.getAbv());
        Assertions.assertEquals("Іспанія", product.getCountry());
        Assertions.assertEquals(1, product.getImageUrls().size());
    }

    private FozzyProvider getProvider() {
        return new FozzyProvider(linkParser(), getProperties());
    }

    private FozzyProperties getProperties() {
        var block = new ProviderBlock();
        block.setType(CategoryTypeEnum.RUM);
        block.setUrl("https://fozzyshop.ua/300354-rom");
        var properties = new FozzyProperties();
        properties.setBlocks(List.of(block));
        return properties;
    }
}
