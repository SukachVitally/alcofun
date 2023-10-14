package com.alco.searchapp.provider;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.properites.AlcomagProperties;
import com.alco.searchapp.provider.dto.LdJsonDto;
import com.alco.searchapp.provider.dto.ProductInfo;
import com.alco.searchapp.provider.dto.ProductPageDto;
import com.alco.searchapp.provider.dto.alcomag.ProductData;
import com.alco.searchapp.utils.AlcoholByVolumeUtil;
import com.alco.searchapp.utils.VolumeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlcomagProvider implements IProvider {
    private static final String URL = "https://alcomag.ua";

    private static final String LIST_PAGE_GET_PARAMETER = "PAGEN_1";

    private static final String LIST_LINK_SELECTOR = ".inner_wrap a.thumb";

    private static final String PROPERTY_ABV_TITLE = "Міцність";

    private static final String PROPERTY_VOLUME_TITLE = "Об`єм";

    private static final String PROPERTY_COUNTRY_TITLE = "Країна";

    private final HtmlLinkParser linkParser;

    private final ObjectMapper mapper;

    private final AlcomagProperties properties;

    @Override
    public ProviderTypeEnum getType() {
        return ProviderTypeEnum.ALCOMAG;
    }

    public Function<Integer, String> createLinkFunction(String url) {
        return (Integer page) -> String.format("%s?PAGEN_1=%s", url, page);
    }

    @Override
    public Flux<ProductPageDto> getProductPages() {
        return Flux.fromIterable(properties.getBlocks())
                .flatMap(block -> Flux
                        .fromIterable(new ProductLinkCollector(createLinkFunction(block.getUrl()), LIST_LINK_SELECTOR))
                        .flatMap(Flux::fromIterable)
                        .map(url -> ProductPageDto.builder()
                                .url(URL + url)
                                .provider(getType())
                                .category(block.getType())
                                .build()));
    }

    @Override
    public Mono<ProductInfo> parsePageInfo(String url) {
        return linkParser.parse(url).map(this::parseDocument);
    }

    private ProductInfo parseDocument(Document document) {
        var info = document.select("script[type=application/ld+json]").stream()
                .map(Element::data)
                .filter(content -> {
                    try {
                        var dto = mapper.readValue(content, LdJsonDto.class);
                        return dto.getContext().contains("schema.org") && dto.getType().equals("Product");
                    } catch (JsonProcessingException e) {
                        return false;
                    }
                })
                .findAny()
                .map(content -> {
                    try {
                        return mapper.readValue(content, ProductData.class);
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                })
                .orElse(new ProductData());

        var properties = document.select(".properties .properties__item").stream().map(element -> {
                var key = Optional.ofNullable(element.selectFirst(".properties__title"))
                        .map(Element::html).orElse(null);
                var value = Optional.ofNullable(element.selectFirst(".properties__value a"))
                        .map(Element::html).orElse(null);
                if (key == null || value == null) {
                    return null;
                }
                return Pair.of(key, value);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

        return ProductInfo.builder()
                .ean(info.getEan())
                .name(info.getName())
                .price(info.getOffers().getPrice())
                .description(info.getDescription())
                .brand(info.getBrand().getName())
                .volume(properties.containsKey(PROPERTY_VOLUME_TITLE)
                        ? VolumeUtils.parse(properties.get(PROPERTY_VOLUME_TITLE))
                        : null)
                .abv(properties.containsKey(PROPERTY_ABV_TITLE)
                        ? AlcoholByVolumeUtil.parse(properties.get(PROPERTY_ABV_TITLE))
                        : null)
                .country(properties.getOrDefault(PROPERTY_COUNTRY_TITLE, null))
                .imageUrls(getImages(document))
                .rating(info.getAggregateRating().getRatingValue())
                .votes(info.getAggregateRating().getReviewCount())
                .build();
    }

    private List<String> getImages(Element doc) {
        return doc.select("img.product-detail-gallery__picture").stream()
                .map(element -> element.attr("data-src"))
                .distinct()
                .map(path -> URL + path)
                .toList();
    }
}
