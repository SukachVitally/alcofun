package com.alco.searchapp.provider;

import com.alco.searchapp.configuration.ObjectMapperConfiguration;
import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

// todo: Maudau api was changed. Need to fix it.
public class MaudauProviderTest {
    @Disabled
    @Test
    public void getPathLinks() {
        var product = getProvider().getList().blockFirst();
        Assertions.assertNotNull(product);
        Assertions.assertNotNull(product.getEan());
        Assertions.assertNotNull(product.getPrice());
        Assertions.assertFalse(product.getImageUrls().isEmpty());
    }

    @Disabled
    @Test
    public void parseLeadingDecimalNumberTest() {
        var link = "https://maudau.com.ua/viski-jack-daniels-honey-35-05-l-610893-md-455027.html";

        var product = getProvider().parse(CategoryTypeEnum.RUM, link);

        Assertions.assertNotNull(product);
        Assertions.assertEquals("Віскі Jack Daniel`s Honey, 35%, 0,5 л (610893)", product.getName());
        Assertions.assertEquals("5099873005101", product.getEan());
        Assertions.assertNotNull(product.getPrice());
        Assertions.assertNotNull(product.getDescription());
        Assertions.assertEquals(500, product.getVolume());
        Assertions.assertEquals("Jack Daniel's", product.getBrand());
        Assertions.assertEquals(35.0f, product.getAbv());
        Assertions.assertEquals("США", product.getCountry());
        Assertions.assertEquals(link, product.getLink());
        Assertions.assertEquals(1, product.getImageUrls().size());
        Assertions.assertEquals(ProviderTypeEnum.MAUDAU, product.getProviderType());
    }

    private MaudauProvider getProvider() {
        return new MaudauProvider(new ObjectMapperConfiguration().objectMapper());
    }
}
