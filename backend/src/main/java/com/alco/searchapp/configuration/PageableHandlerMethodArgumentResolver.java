package com.alco.searchapp.configuration;

import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class PageableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String DEFAULT_PAGE = "1";
    private static final String DEFAULT_SIZE = "24";
    private static final Integer MAX_SIZE = 60;
    private static final int MIN_PAGE_NUMBER = 1;
    private static final int MIN_SIZE = 1;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.equals(parameter.getParameterType());
    }

    @Override
    public Mono<Object> resolveArgument(MethodParameter methodParameter, BindingContext bindingContext, ServerWebExchange serverWebExchange) {
        return Mono.just(
                PageRequest.of(
                        getPageNumber(serverWebExchange),
                        getSize(serverWebExchange),
                        getSort(serverWebExchange)
                )
        );
    }

    private int getPageNumber(ServerWebExchange serverWebExchange) {
        List<String> pageValues = serverWebExchange.getRequest().getQueryParams()
                .getOrDefault("page", List.of(DEFAULT_PAGE));
        var page = Integer.parseInt(pageValues.get(0));

        return Math.max(page, MIN_PAGE_NUMBER);
    }

    private int getSize(ServerWebExchange serverWebExchange) {
        List<String> sizeValues = serverWebExchange.getRequest().getQueryParams()
                .getOrDefault("size", List.of(DEFAULT_SIZE));
        return Math.max(MIN_SIZE, Math.min(Integer.parseInt(sizeValues.get(0)), MAX_SIZE));
    }

    private Sort getSort(ServerWebExchange serverWebExchange) {
        String sortParam = serverWebExchange.getRequest().getQueryParams().getFirst("sort");
        Sort sort = Sort.unsorted();

        if (sortParam != null) {
            String[] parts = sortParam.split(",");
            if (parts.length == 2) {
                String property = parts[0];
                Sort.Direction direction = Sort.Direction.fromString(parts[1]);
                sort = Sort.by(direction, property);
            }
        }
        return sort;
    }
}