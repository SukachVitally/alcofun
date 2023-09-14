package com.alco.searchapp.provider;

import com.alco.searchapp.properites.ProviderProperty;
import com.alco.searchapp.provider.dto.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderCollector {
    private final ProviderProperty property;

    private final List<IProvider> providers;

    private final Validator validator;

    private final ObjectMapper mapper;

    public Flux<Product> collect() {
        return Flux.fromIterable(providers)
                .filter(provider -> property.getList().contains(provider.getType()))
                .flatMap(IProvider::getList)
                .filter(product -> {
                    var constraints = validator.validate(product);
                    if (constraints.size() == 0) {
                        return true;
                    }
                    var message = constraints.stream()
                            .map(constraint -> String.format("%s - %s", constraint.getPropertyPath(), constraint.getMessage()))
                            .collect(Collectors.joining(""));
                    logError(product, message);
                    return false;
                });
    }

    private void logError(Product product, String message) {
        var loggableProduct = Product.builder()
                .ean(product.getEan())
                .category(product.getCategory())
                .price(product.getPrice())
                .name(product.getName())
                .providerType(product.getProviderType())
                .link(product.getLink())
                .build();
        try {
            log.error(String.format("Product %s is invalid (%s)", mapper.writeValueAsString(loggableProduct), message));
        } catch (JsonProcessingException e) {
            log.error(String.format("Product for page %s is invalid (%s)", product.getLink(), message));
        }
    }
}
