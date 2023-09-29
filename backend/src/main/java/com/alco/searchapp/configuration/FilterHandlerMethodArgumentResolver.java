package com.alco.searchapp.configuration;

import com.alco.searchapp.dto.Filterable;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class FilterHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Filterable.class.equals(parameter.getParameterType());
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter methodParameter, BindingContext bindingContext, ServerWebExchange serverWebExchange) {
        List<Long> categories = serverWebExchange.getRequest().getQueryParams().getOrDefault("categories", List.of()).stream()
                .map(Long::valueOf)
                .toList();
        List<Long> providers = serverWebExchange.getRequest().getQueryParams().getOrDefault("providers", List.of()).stream()
                .map(Long::valueOf)
                .toList();

        List<Long> characteristics = serverWebExchange.getRequest().getQueryParams().getOrDefault("characteristics", List.of()).stream()
                .map(Long::valueOf)
                .toList();
        List<String> search = serverWebExchange.getRequest().getQueryParams().getOrDefault("search", List.of());

        Double priceMin = serverWebExchange.getRequest().getQueryParams().getOrDefault("price_min", List.of())
                .stream().map(Double::parseDouble).findAny().orElse(null);

        Double priceMax = serverWebExchange.getRequest().getQueryParams().getOrDefault("price_max", List.of())
                .stream().map(Double::parseDouble).findAny().orElse(null);

        return Mono.just(
                Filterable.builder()
                        .search(!search.isEmpty() ? search.get(0) : null)
                        .categories(categories)
                        .providers(providers)
                        .characteristics(characteristics)
                        .price(new Filterable.Price(priceMin, priceMax))
                        .build()
        );
    }
}