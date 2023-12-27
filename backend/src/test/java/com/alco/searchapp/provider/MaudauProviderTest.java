package com.alco.searchapp.provider;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.properites.ProviderBlock;
import com.alco.searchapp.properites.provider.MaudauProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.util.List;

public class MaudauProviderTest extends AbstractProviderTest {
    @Test
    public void getPathLinks() {
        var productPage = getProvider().getProductPages().blockFirst();
        Assertions.assertNotNull(productPage);
    }

    @Test
    @Disabled
    public void parseLeadingDecimalNumberTest() {
        var link = "view-source:https://maudau.com.ua/product/viski-jack-daniels-honey-35-05-l-610893";

        var product = getProvider().parsePageInfo(link).block();

        Assertions.assertNotNull(product);
        Assertions.assertEquals("Віскі Jack Daniel`s Honey, 35%, 0,5 л (610893)", product.getName());
        Assertions.assertEquals("5099873005101", product.getEan());
        Assertions.assertNotNull(product.getPrice());
        Assertions.assertNotNull(product.getDescription());
        Assertions.assertEquals(500, product.getVolume());
        Assertions.assertEquals("Jack Daniel's", product.getBrand());
        Assertions.assertEquals(35.0f, product.getAbv());
        Assertions.assertEquals("США", product.getCountry());
        Assertions.assertEquals(1, product.getImageUrls().size());
    }

    private MaudauProvider getProvider() {
        var block = new ProviderBlock();
        block.setType(CategoryTypeEnum.WHISKEY);
        block.setUrl("https://maudau.com.ua/category/viski/");
        var properties = new MaudauProperties();
        properties.setBlocks(List.of(block));

        return new MaudauProvider(objectMapper(), linkParser(), properties);
    }
}
