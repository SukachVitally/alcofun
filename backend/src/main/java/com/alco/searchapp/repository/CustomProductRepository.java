package com.alco.searchapp.repository;

import com.alco.searchapp.entity.DenormalizedProductEntity;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class CustomProductRepository {
    private final DatabaseClient connection;

    public Flux<DenormalizedProductEntity> generateDenormalizedStructure(LocalDateTime startTime) {
        var sql = """
            SELECT p.id, gpp.providers, p.ean, p.name, p.category_id, gpc.characteristic_ids
            FROM (
                SELECT tpp.product_id, json_object_agg(tpp.provider_id, tpp.price) AS providers
                FROM (
                    SELECT pp.product_id,
                           pp.provider_id,
                           pp.price
                    FROM provider_product pp
                    JOIN (
                        SELECT product_id,
                               provider_id,
                               MAX(created_at) AS max_created_at
                        FROM provider_product
                        WHERE created_at > :startTime
                        GROUP BY product_id, provider_id
                    ) max_dates
                    ON pp.product_id = max_dates.product_id
                       AND pp.provider_id = max_dates.provider_id
                       AND pp.created_at = max_dates.max_created_at
                    WHERE pp.created_at > :startTime
                ) as tpp
                GROUP BY product_id
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
                .map(row -> {
                    var entity = new DenormalizedProductEntity();
                    entity.setId(row.get("id", Long.class));
                    entity.setEan(row.get("ean", String.class));
                    entity.setName(row.get("name", String.class));
                    entity.setCategoryId(row.get("category_id", Long.class));
                    entity.setCharacteristicIds(parseLongList(row.get("characteristic_ids", String.class)));
                    entity.setProviders(parseDoubleMap(row.get("providers", String.class)));
                    return entity;
                })
                .all();
    }

    public Flux<List<Long>> getCharacteristicBlocks(List<Long> ids) {
        var sql = """
                SELECT type_id, array_agg(id) AS ids
                FROM characteristic
                WHERE id in (:ids)
                GROUP BY type_id
            """;

        return connection.sql(sql)
                .bind("ids", ids)
                .map(row -> parseLongList(row.get("ids", String.class)))
                .all();
    }

    private static List<Long> parseLongList(String input) {
        input = input.replaceAll("[{}]", "");
        return Arrays.stream(input.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    private static Map<Long, Double> parseDoubleMap(String input) {
        Map<Long, Double> parsedMap = new HashMap<>();
        input = input.replaceAll("[{\"}]", "");

        Pattern pattern = Pattern.compile(",");
        String[] keyValuePairs = pattern.split(input);

        for (String pair : keyValuePairs) {
            String[] parts = pair.split(":");
            if (parts.length == 2) {
                Long key = Long.parseLong(parts[0].trim());
                double value = Double.parseDouble(parts[1].trim());
                parsedMap.put(key, value);
            }
        }

        return parsedMap;
    }
}
