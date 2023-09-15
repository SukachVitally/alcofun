package com.alco.searchapp.repository;

import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.entity.ProductEntity;
import io.r2dbc.spi.Readable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CustomProductRepository {
    private final DatabaseClient connection;

    public Flux<ProductEntity> findAll(Filterable filterable, Pageable pageable, LocalDateTime startTime) {
        var query = createQuery(filterable, false, startTime);

        var sql = query.sql + " offset :offset limit :limit";
        var bindParameters = query.bindParameters;
        bindParameters.put("offset", (pageable.getPageNumber() - 1) * pageable.getPageSize());
        bindParameters.put("limit", pageable.getPageSize());

        var spec = connection.sql(sql);
        for (Map.Entry<String, Object> bindParameter: bindParameters.entrySet()) {
            spec = spec.bind(bindParameter.getKey(), bindParameter.getValue());
        }

        return spec
                .map(this::map)
                .all();
    }

    public Mono<Long> count(Filterable filterable, LocalDateTime startTime) {
        var query = createQuery(filterable, true, startTime);

        var spec = connection.sql(query.sql);
        for (Map.Entry<String, Object> bindParameter: query.bindParameters.entrySet()) {
            spec = spec.bind(bindParameter.getKey(), bindParameter.getValue());
        }

        return spec
                .map((row, metadata) -> row.get(0, Long.class))
                .one()
                .switchIfEmpty(Mono.just(0L));
    }

    // todo: Refactor it!!!
    private Query createQuery(Filterable filterable, boolean isCount, LocalDateTime startTime) {
        Map<String, Object> bindParameters = new HashMap<>();
        bindParameters.put("startTime", startTime);
        String providerSql = "SELECT product_id FROM provider_product WHERE created_at > :startTime";

        if (filterable.getPrice() != null) {
            if (filterable.getPrice().min() != null && filterable.getPrice().max() != null) {
                providerSql += " AND price > :minPrice AND price < :maxPrice";
                bindParameters.put("minPrice", filterable.getPrice().min());
                bindParameters.put("maxPrice", filterable.getPrice().max());
            } else if (filterable.getPrice().min() != null) {
                providerSql = " AND price > :minPrice";
                bindParameters.put("minPrice", filterable.getPrice().min());
            } else if (filterable.getPrice().max() != null) {
                providerSql = " AND price < :maxPrice";
                bindParameters.put("maxPrice", filterable.getPrice().max());
            }
        }

        if (filterable.getProviders() != null && !filterable.getProviders().isEmpty()) {
            providerSql += " AND provider_id in (:providerIds)";
            bindParameters.put("providerIds", filterable.getProviders());
        }

        String sql = String.format("SELECT %s FROM product WHERE id IN (%s)", isCount ? "COUNT(*)" : "*", providerSql);

        if (filterable.getCategories() != null && !filterable.getCategories().isEmpty()) {
            sql += " AND category_id in (:categoryIds)";
            bindParameters.put("categoryIds", filterable.getCategories());
        }

        if (filterable.getSearch() != null && !filterable.getSearch().equals("")) {
            sql += " AND (ean = :ean OR name ILIKE :pattern)";
            bindParameters.put("ean", filterable.getSearch());
            bindParameters.put("pattern", "%" + filterable.getSearch() + "%");
        }

        if (filterable.getCharacteristics() != null && !filterable.getCharacteristics().isEmpty()) {
            sql += " AND id IN (SELECT product_id FROM product_characteristic WHERE characteristic_id in (:characteristicIds))";
            bindParameters.put("characteristicIds", filterable.getCharacteristics());
        }

        return new Query(sql, bindParameters);
    }

    private ProductEntity map(Readable row) {
        var entity = new ProductEntity();
        entity.setId(row.get("id", Long.class));
        entity.setName(row.get("name", String.class));
        entity.setEan(row.get("ean", String.class));
        entity.setDescription(row.get("description", String.class));
        entity.setCategoryId(row.get("category_id", Long.class));
        entity.setCreatedAt(row.get("created_at", LocalDateTime.class));

        return entity;
    }

    record Query (String sql, Map<String, Object> bindParameters) {
    }
}
