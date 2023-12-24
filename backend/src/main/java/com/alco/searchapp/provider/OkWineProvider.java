package com.alco.searchapp.provider;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.properites.OkWineProperties;
import com.alco.searchapp.provider.dto.ProductInfo;
import com.alco.searchapp.provider.dto.ProductPageDto;
import com.alco.searchapp.provider.dto.okwine.ListResponse;
import com.alco.searchapp.provider.dto.okwine.ProductData;
import com.alco.searchapp.utils.AlcoholByVolumeUtil;
import com.alco.searchapp.utils.VolumeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class OkWineProvider implements IProvider {
    private static final String PRODUCT_URL_PREFIX = "https://okwine.ua/ua/product/";

    private static final String IMAGE_URL_PREFIX = "https://content.okwine.ua/files/";

    private static final Pattern ABV_NAME_PATTERN = Pattern.compile("\\s([\\d.]*?)%");

    private static final String CHARACTERISTIC_COUNTRY = "Країна";

    private static final String CHARACTERISTIC_VOLUME = "Об'єм";

    private static final String CHARACTERISTIC_MANUFACTURE = "Виробник";

    private final ObjectMapper mapper;

    private final LinkParser linkParser;

    private final OkWineProperties properties;

    @Override
    public Flux<ProductPageDto> getProductPages() {
        return Flux.fromIterable(properties.getBlocks())
            .flatMap(block -> linkParser.parseJson(block.getUrl(), ListResponse.class)
                    .filter(response -> response.getData() != null && response.getData().getMaxPage() != null)
                    .flatMapMany(response -> Flux
                            .fromStream(IntStream.range(1, response.getData().getMaxPage() + 1).boxed()))
                    .flatMap(page -> linkParser.parseJson(String.format("%s&page=%s", block.getUrl(), page), ListResponse.class))
                    .filter(response -> response.getData() != null && response.getData().getData() != null)
                    .flatMap(response -> Flux.fromStream(response.getData().getData().stream()
                            .map(product -> PRODUCT_URL_PREFIX + product.getUrl())))
                    .map(url -> ProductPageDto.builder()
                            .url(url)
                            .provider(getType())
                            .category(block.getType())
                            .build()));
    }

    @Override
    public ProviderTypeEnum getType() {
        return ProviderTypeEnum.OKWINE;
    }

    @Override
    public Mono<ProductInfo> parsePageInfo(String url) {
        return linkParser
                .parseHtml(url)
                .map(this::parseDocument);
    }

    public ProductInfo parseDocument(Document document) {
            var element  = document.selectFirst("#__NEXT_DATA__");
            if (element == null) {
                return ProductInfo.builder().build();
            }
            var content = element.data();
            try {
                var data = mapper.readValue(content, ProductData.class);
                var productData = data.getProps().getPageProps().getHydrationData().getProductPageStore().getProduct();
                var description = Jsoup.clean(productData.getMeta().getDescription(), "", Safelist.none(),
                        new Document.OutputSettings().prettyPrint(true));

                return ProductInfo.builder()
                        .name(productData.getMeta().getName())
                        .imageUrls(productData.getImages().stream().map(path -> IMAGE_URL_PREFIX + path).toList())
                        .ean(productData.getBarcodes().stream().findFirst().orElse(null))
                        .description(description)
                        .price(productData.getPrices().getPrice())
                        .abv(parseAbv(productData.getMeta().getName()))
                        .volume(findVolume(productData.getCharacteristics()))
                        .country(findCharacteristic(productData.getCharacteristics(), CHARACTERISTIC_COUNTRY))
                        .brand(findCharacteristic(productData.getCharacteristics(), CHARACTERISTIC_MANUFACTURE))
                        .build();
            } catch (JsonProcessingException e) {
                return ProductInfo.builder().build();
            }
    }

    private Float parseAbv(String name) {
        Matcher matcher = ABV_NAME_PATTERN.matcher(name);
        if (matcher.find()) {
            String percentageValue = matcher.group(1);
            return AlcoholByVolumeUtil.parse(percentageValue);
        }

        return null;
    }

    private String findCharacteristic(List<ProductData.Characteristic> list, String type) {
        return list.stream()
                .filter(characteristic -> characteristic.getKey().getName().equals(type))
                .map(characteristic -> characteristic.getValues().stream()
                        .findFirst().orElse(null))
                .filter(Objects::nonNull)
                .map(obj -> obj.getValue().getUa())
                .findAny()
                .orElse(null);
    }

    private Integer findVolume(List<ProductData.Characteristic> list) {
        return list.stream()
                .filter(characteristic -> characteristic.getKey().getName().equals(CHARACTERISTIC_VOLUME))
                .flatMap(characteristic -> characteristic.getValues().stream())
                .map(obj -> obj.getValue().getUa())
                .map(VolumeUtils::parse)
                .findAny()
                .orElse(null);
    }
}
