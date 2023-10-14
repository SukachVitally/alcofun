package com.alco.searchapp.provider;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.exception.ProductProviderNotSupport;
import com.alco.searchapp.provider.dto.Product;
import com.alco.searchapp.provider.dto.ProductInfo;
import com.alco.searchapp.provider.dto.ProductPageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderCollector {
    private final List<IProvider> providers;

    private final Validator validator;

    private final ObjectMapper mapper;

    public Flux<ProductPageDto> collect(List<ProviderTypeEnum> providerTypes) {
        return Flux.fromIterable(providers)
                .filter(provider -> providerTypes.contains(provider.getType()))
                .flatMap(IProvider::getProductPages);
    }

    public Mono<Product> parse(ProductPageDto pageDto) {
        return providers.stream().filter(item -> item.getType().equals(pageDto.getProvider()))
                .findAny()
                .map(provider -> parseProductInfo(provider, pageDto.getUrl())
                        .map(productInfo -> Product.builder()
                                .category(pageDto.getCategory())
                                .providerType(pageDto.getProvider())
                                .url(pageDto.getUrl())
                                .info(productInfo)
                                .build()))
                .orElse(Mono.error(new ProductProviderNotSupport(pageDto.getProvider())));
    }

    private Mono<ProductInfo> parseProductInfo(IProvider provider, String url) {
        return provider.parsePageInfo(url)
                .filter(productInfo -> {
                    var constraints = validator.validate(productInfo);
                    if (constraints.size() == 0) {
                        return true;
                    }
                    var message = constraints.stream()
                            .map(constraint -> String.format("%s - %s", constraint.getPropertyPath(), constraint.getMessage()))
                            .collect(Collectors.joining(""));
                    logError(productInfo, url, message);
                    return false;
                });
    }

    private void logError(ProductInfo productInfo, String url, String message) {
        try {
            String preview = mapper.writeValueAsString(productInfo);
            log.error(String.format("ProductInfo from %s %s is invalid (%s)", url, preview, message));
        }
        catch (JsonProcessingException e) {
            log.error(String.format("ProductInfo for page %s is invalid", url));
        }
    }
}
