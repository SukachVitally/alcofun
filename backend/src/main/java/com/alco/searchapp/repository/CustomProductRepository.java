package com.alco.searchapp.repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
@RequiredArgsConstructor
public class CustomProductRepository {
    private final DatabaseClient connection;

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
}
