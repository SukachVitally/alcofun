package com.alco.searchapp.provider;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.properites.provider.MaudauProperties;
import com.alco.searchapp.provider.dto.ProductInfo;
import com.alco.searchapp.provider.dto.ProductPageDto;
import com.alco.searchapp.provider.dto.maudau.MaudauData;
import com.alco.searchapp.utils.AlcoholByVolumeUtil;
import com.alco.searchapp.utils.VolumeUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.jsoup.nodes.Element;

// todo: Maudau product doesn't have ean number in new API. Require another
@Slf4j
@RequiredArgsConstructor
public class MaudauProvider implements IProvider  {
    private static final String LIST_LINK_SELECTOR = "a.chakra-link";
    private static final String PRODUCT_PREFIX = "https://maudau.com.ua";
    private static final String PAGE_DATA_SELECTOR = "script";
    private static final String IMAGE_PREFIX = "https://sf-api.maudau.com.ua/img/0/0/resize/catalog/product";

    private final ObjectMapper mapper;

    private final LinkParser linkParser;

    private final MaudauProperties properties;

    public Function<Integer, String> createLinkFunction(String url) {
        return (Integer page) -> String.format("%spage=%s;availability=in_stock", url, page);
    }

    @Override
    public Flux<ProductPageDto> getProductPages() {
        return Flux.fromIterable(properties.getBlocks())
                .flatMap(block -> Flux
                        .fromIterable(new ProductLinkCollector(createLinkFunction(block.getUrl()), LIST_LINK_SELECTOR))
                        .flatMap(Flux::fromIterable)
                        .filter(link -> link.startsWith("/product/"))
                        .map(url -> PRODUCT_PREFIX + url)
                        .map(url -> ProductPageDto.builder()
                                .url(url)
                                .provider(getType())
                                .category(block.getType())
                                .build()));
    }

    @Override
    public Mono<ProductInfo> parsePageInfo(String url) {
        return linkParser
                .parseHtml(url)
                .map(this::parseDocument);
    }

    @Override
    public ProviderTypeEnum getType() {
        return ProviderTypeEnum.MAUDAU;
    }

    public ProductInfo parseDocument(Document doc) {
        return doc.select(PAGE_DATA_SELECTOR).stream()
                .map(Element::data)
                .filter(data -> data.contains("window.__INITIAL_STATE__="))
                .findAny()
                .map(data -> data
                        .replace("window.__INITIAL_STATE__=", "")
                        .replace(":!1", ":null")
                        .replace(":!0", ":null")
                        .replaceAll(",function.*", ""))
                .map(tag -> Jsoup.clean(tag, Safelist.none()))
                .map(content -> {
                    try {
                        return mapper.readValue(content, MaudauData.class);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .map(data -> {
                    var productData = data.getProduct().getCurrent();

                    return ProductInfo.builder()
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
