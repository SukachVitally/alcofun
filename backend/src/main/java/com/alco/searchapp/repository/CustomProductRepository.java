package com.alco.searchapp.repository;

import com.alco.searchapp.dto.Filterable;
import com.alco.searchapp.dto.ProductSearchDto;
import com.alco.searchapp.entity.ProductEntity;
import io.r2dbc.spi.Readable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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

    public Flux<ProductSearchDto> generateDenormalizedStructure(LocalDateTime startTime) {
        var sql = """
                SELECT p.id, gpp.providers, p.ean, p.name, p.category_id, gpc.characteristic_ids
                FROM (
                    SELECT pp.product_id,
                           pp.provider_id,
                           pp.price
                    FROM provider_product pp
                    WHERE created_at > :startTime
                    JOIN (
                        SELECT product_id,
                               provider_id,
                               MAX(created_at) AS max_created_at
                        FROM provider_product
                        GROUP BY product_id, provider_id
                    ) max_dates
                    ON pp.product_id = max_dates.product_id
                       AND pp.provider_id = max_dates.provider_id
                       AND pp.created_at = max_dates.max_created_at;
                ) gpp
                JOIN product p ON p.id = gpp.product_id
                JOIN (
                    SELECT pc.product_id, array_agg(pc.characteristic_id) AS characteristic_ids
                    FROM product_characteristic pc
                    GROUP BY pc.product_id
                ) gpc ON gpc.product_id = p.id
                """;

        return connection.sql(sql)
                .bind("startTime", startTime)
                .map(row -> ProductSearchDto.builder()
                        .id(row.get("id", Long.class))
                        .ean(row.get("ean", String.class))
                        .categoryId(row.get("category_id", Long.class))
                        .characteristicIds(row.get("characteristic_ids", ListLong.class))
                        .providers(row.get("providers", MapPrice.class))
                        .build()
                )
                .all();
    }

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
            sql += """
                     AND id IN (
                       WITH type_counts AS (
                          SELECT pc.product_id, c.type_id
                          FROM product_characteristic pc
                          JOIN characteristic c ON c.id = pc.characteristic_id
                          WHERE pc.characteristic_id IN (:characteristicIds)
                          GROUP BY pc.product_id, c.type_id
                      ),
                      group_count AS (
                          SELECT COUNT(DISTINCT type_id) AS ct
                          FROM type_counts
                      )
                      SELECT tt.product_id
                      FROM (
                          SELECT product_id, COUNT(*) AS cnt
                          FROM type_counts
                          GROUP BY product_id
                      ) AS tt
                      CROSS JOIN group_count
                      WHERE tt.cnt = group_count.ct;
                    )
                    """;
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

    record Query (String sql, java.util.Map<String, Object> bindParameters) {
    }

    private interface ListLong extends List<Long> {

    }

    private interface MapPrice extends Map<String, Double> {

    }
}
