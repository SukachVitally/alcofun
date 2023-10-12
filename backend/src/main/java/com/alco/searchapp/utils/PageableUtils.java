package com.alco.searchapp.utils;

import java.util.Map;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PageableUtils {
    private final Map<String, String> SORT_PROPERTIES_MAP = Map.of("name", "name.keyword",
            "created_at", "id",
            "rating", "internalRating",
            "price", "price",
            "hot", "percent" // todo: Add hot search sorting
    );

    public Pageable mapToInternal(Pageable pageable) {
        var sort = Sort.by(pageable.getSort().stream()
                .filter(order -> SORT_PROPERTIES_MAP.containsKey(order.getProperty()))
                .map(order -> new Sort.Order(order.getDirection(), SORT_PROPERTIES_MAP.get(order.getProperty())))
                .toList());
        return PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sort);
    }
}
