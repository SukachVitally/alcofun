package com.alco.searchapp.provider;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.exception.ProviderException;
import com.alco.searchapp.provider.dto.Product;
import com.alco.searchapp.provider.dto.okwine.ListResponse;
import com.alco.searchapp.provider.dto.okwine.ProductData;
import com.alco.searchapp.utils.AlcoholByVolumeUtil;
import com.alco.searchapp.utils.VolumeUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class OkWineProvider implements IProvider {
    private static final List<Pair<String, CategoryTypeEnum>> PATHS = List.of(
            Pair.of("https://okwine.ua/api/v1/filter/products?14=826&city=61e159f3ab2700007200435f", CategoryTypeEnum.ABSENT),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=1404&city=61e159f3ab2700007200435f", CategoryTypeEnum.CHACHA),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=1406&city=61e159f3ab2700007200435f", CategoryTypeEnum.LIQUEUR),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=2618&city=61e159f3ab2700007200435f", CategoryTypeEnum.LIQUEUR),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=2780&city=61e159f3ab2700007200435f", CategoryTypeEnum.TEQUILA),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=6604&city=61e159f3ab2700007200435f", CategoryTypeEnum.COGNAC),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=712&city=61e159f3ab2700007200435f", CategoryTypeEnum.LIQUEUR),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=717&city=61e159f3ab2700007200435f", CategoryTypeEnum.LIQUEUR),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=779&city=61e159f3ab2700007200435f", CategoryTypeEnum.WHISKEY),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=786&city=61e159f3ab2700007200435f", CategoryTypeEnum.COGNAC),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=808&city=61e159f3ab2700007200435f", CategoryTypeEnum.ARMAGNAC),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=818&city=61e159f3ab2700007200435f", CategoryTypeEnum.RUM),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=824&city=61e159f3ab2700007200435f", CategoryTypeEnum.VODKA),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=829&city=61e159f3ab2700007200435f", CategoryTypeEnum.LIQUEUR),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=836&city=61e159f3ab2700007200435f", CategoryTypeEnum.GIN),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=842&city=61e159f3ab2700007200435f", CategoryTypeEnum.TEQUILA),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=878&city=61e159f3ab2700007200435f", CategoryTypeEnum.LIQUEUR),
            Pair.of("https://okwine.ua/api/v1/filter/products?14=948&city=61e159f3ab2700007200435f", CategoryTypeEnum.GRAPPA)
            );

    private static final String PRODUCT_URL_PREFIX = "https://okwine.ua/ua/product/";

    private static final String IMAGE_URL_PREFIX = "https://content.okwine.ua/files/";

    private static final Pattern ABV_NAME_PATTERN = Pattern.compile("\\s([\\d.]*?)%");

    private final ObjectMapper mapper;

    private final WebClient client;

    private final HtmlLinkParser linkParser;

    @Override
    public Flux<Product> getList() {
        return Flux.fromIterable(PATHS)
                .flatMap(pair -> getContent(pair.getFirst())
                        .flatMapMany(content -> {
                            try {
                                var response = mapper.readValue(content, ListResponse.class);
                                return Flux.fromStream(IntStream.range(1, response.getData().getMaxPage())
                                        .boxed());
                            } catch (IOException e) {
                                return Flux.empty();
                            }
                        })
                        .flatMap(page -> getContent(String.format("%s&page=%s", pair.getFirst(), page)))
                        .flatMap(content -> {
                            try {
                                var response = mapper.readValue(content, ListResponse.class);
                                Stream<String> stream = response.getData().getData()
                                    .stream()
                                    .map(product -> PRODUCT_URL_PREFIX + product.getUrl());
                                return Flux.fromStream(stream);
                            } catch (IOException e) {
                                return Flux.empty();
                            }
                        })
                        .flatMap(url -> linkParser.parse(url)
                                .map(document -> this.parse(document, pair.getSecond(), url))));
    }

    @Override
    public ProviderTypeEnum getType() {
        return ProviderTypeEnum.OKWINE;
    }

    private Mono<String> getContent(String url) {
        return client.get()
                .uri(url)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> {
                            if (response.statusCode().is4xxClientError()) {
                                return Mono.error(new ProviderException("Custom 4xx error message"));
                            } else if (response.statusCode().is5xxServerError()) {
                                return Mono.error(new ProviderException("Custom 5xx error message"));
                            } else {
                                return Mono.error(new ProviderException("Custom error message"));
                            }
                        }
                )
                .bodyToMono(String.class);
    }

    public Product parse(Document document, CategoryTypeEnum category, String url) {
            var element  = document.selectFirst("#__NEXT_DATA__");
            if (element == null) {
                log.error(String.format("Cannot find product info for page %s", url));
                return null;
            }
            var content = element.data();
            try {
                var data = mapper.readValue(content, ProductData.class);
                var productData = data.getProps().getPageProps().getHydrationData().getProductPageStore().getProduct();

                return Product.builder()
                        .providerType(getType())
                        .link(url)
                        .category(category)
                        .name(productData.getMeta().getName())
                        .imageUrls(productData.getImages().stream().map(path -> IMAGE_URL_PREFIX + path).toList())
                        .ean(productData.getBarcodes().get(0))
                        .description(productData.getMeta().getDescription())
                        .price(productData.getPrices().getPrice())
                        .abv(parseAbv(productData.getMeta().getName()))
                        .volume(productData.getCharacteristics().stream()
                                .filter(characteristic -> characteristic.getKey().getName().equals("Об'єм"))
                                .map(characteristic -> {
                                    var rawValue = characteristic.getValues().get(0).getValue().getUa();
                                    return VolumeUtils.parse(rawValue);
                                })
                                .findAny()
                                .orElse(null))
                        .country(productData.getCharacteristics().stream()
                                .filter(characteristic -> characteristic.getKey().getName().equals("Країна"))
                                .map(characteristic -> characteristic.getValues().get(0).getValue().getUa())
                                .findAny()
                                .orElse(null))
                        .brand(productData.getCharacteristics().stream()
                                .filter(characteristic -> characteristic.getKey().getName().equals("Виробник"))
                                .map(characteristic -> characteristic.getValues().get(0).getValue().getUa())
                                .findAny()
                                .orElse(null))
                        .build();
            } catch (JsonProcessingException e) {
                log.error(String.format("Cannot parse link content %s", url), e);
                return null;
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
}
