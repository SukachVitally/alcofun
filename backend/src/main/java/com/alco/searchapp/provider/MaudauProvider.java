package com.alco.searchapp.provider;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.provider.dto.Product;
import com.alco.searchapp.provider.dto.maudau.MaudauData;
import com.alco.searchapp.utils.AlcoholByVolumeUtil;
import com.alco.searchapp.utils.StringUtils;
import com.alco.searchapp.utils.VolumeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@Component
public class MaudauProvider implements IProvider {
    private static final List<Pair<String, CategoryTypeEnum>> PATHS = List.of(
            Pair.of("https://maudau.com.ua/category/viski/", CategoryTypeEnum.WHISKEY),
            Pair.of("https://maudau.com.ua/category/brendi-i-koniak", CategoryTypeEnum.COGNAC),
            Pair.of("https://maudau.com.ua/category/horilka", CategoryTypeEnum.VODKA),
            Pair.of("https://maudau.com.ua/category/dzhyn", CategoryTypeEnum.GIN),
            Pair.of("https://maudau.com.ua/category/rom", CategoryTypeEnum.RUM),
            Pair.of("https://maudau.com.ua/category/tekila", CategoryTypeEnum.TEQUILA),
            Pair.of("https://maudau.com.ua/category/likery-i-nalyvky", CategoryTypeEnum.LIQUEUR),
            Pair.of("https://maudau.com.ua/category/sake-i-aziiskyi-alkohol", CategoryTypeEnum.SAKE),
            Pair.of("https://maudau.com.ua/category/nastoianky-balzamy-bittery", CategoryTypeEnum.LIQUEUR),
            Pair.of("https://maudau.com.ua/category/vyno", CategoryTypeEnum.VINE),
            Pair.of("https://maudau.com.ua/category/ihrysti-vyna", CategoryTypeEnum.CHAMPAGNE),
            Pair.of("https://maudau.com.ua/category/vermuty", CategoryTypeEnum.VERMOUTH)
            );

    private static final String LIST_LINK_SELECTOR = "a.product-link-image";
    private static final String LIST_PAGE_GET_PARAMETER = "page";
    private static final String LIST_PAGE_GET_PARAMETER_SUFFIX = ";msi_is_available=in_stock";

    private static final String PRODUCT_PREFIX = "https://maudau.com.ua";
    private static final String PAGE_DATA_SELECTOR = "script";
    private static final String IMAGE_PREFIX = "https://sf-api.maudau.com.ua/img/0/0/resize/catalog/product";

    private final ObjectMapper mapper;

    @Override
    public Flux<Product> getList() {
        return Flux.fromIterable(PATHS)
                .flatMap(pair -> Flux
                        .fromIterable(new ProductLinkCollector(pair.getFirst(), LIST_LINK_SELECTOR, LIST_PAGE_GET_PARAMETER, LIST_PAGE_GET_PARAMETER_SUFFIX))
                        .flatMap(Flux::fromIterable)
                        .map(url -> PRODUCT_PREFIX + url)
                        .mapNotNull(url -> parse(pair.getSecond(), url)));
    }

    @Override
    public ProviderTypeEnum getType() {
        return ProviderTypeEnum.MAUDAU;
    }

    public Product parse(CategoryTypeEnum category, String url) {
        try {
            Document doc = Jsoup.connect(url).get();

            return doc.select(PAGE_DATA_SELECTOR).stream()
                    .map(Element::data)
                    .filter(data -> data.contains("window.__INITIAL_STATE__="))
                    .findAny()
                    .map(data -> data
                            .replace("window.__INITIAL_STATE__=", "")
                            .replace(":!1", ":null")
                            .replace(":!0", ":null")
                            .replaceAll(",function.*", ""))
                    .map(StringUtils::removeAllHtmlTags)
                    .map(content -> {
                        try {
                            return mapper.readValue(content, MaudauData.class);
                        } catch (IOException e) {
                            log.error(String.format("Cannot parse data on page %s", url), e);
                            return null;
                        }
                    })
                    .map(data -> {
                        var productData = data.getProduct().getCurrent();

                        return Product.builder()
                                .providerType(getType())
                                .link(url)
                                .category(category)
                                .name(productData.getName())
                                .imageUrls(List.of(IMAGE_PREFIX + productData.getImage()))
                                .ean(productData.getBarcode())
                                .description(productData.getDescription())
                                .price(productData.getPrice())
                                .brand(productData.getBrand())
                                .country(productData.getCountry())
                                .volume(parseVolume(productData.getName()))
                                .abv(parseAbvInfo(productData.getName()))
                                .build();
                    })
                    .orElse(null);
        } catch (IOException e) {
            log.error(String.format("Cannot parse page %s", url), e);
            return null;
        }
    }

    private Float parseAbvInfo(String name) {
        var info = name.split(", ");
        if (info.length != 3) {
            return null;
        }
        return AlcoholByVolumeUtil.parse(info[1]);
    }

    private Integer parseVolume(String name) {
        var info = name.split(", ");
        if (info.length != 3) {
            return null;
        }
        var rawValue = info[2].replaceAll(" л \\(.*", "").replace(",", ".");
        return VolumeUtils.parse(rawValue);
    }
}
