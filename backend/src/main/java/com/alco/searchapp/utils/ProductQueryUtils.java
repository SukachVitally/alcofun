package com.alco.searchapp.utils;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductQueryUtils {
    public Query createCategoriesQuery(List<Long> ids) {
        var values = ids.stream().map(FieldValue::of).toList();
        return new Query.Builder().terms(t -> t
                        .field("categoryId")
                        .terms(tt -> tt.value(values)))
                .build();
    }

    public Query createSearchQuery(String search) {
        return new Query.Builder().bool(b -> b
                .should(List.of(
                    new Query.Builder().term(m -> m.field("ean").value(search)).build(),
                    new Query.Builder().match(p -> p.field("name")
                        .query(search)
                        .operator(Operator.And)
                    ).build()
                )
            ))
            .build();
    }

    public Query createCharacteristicsQuery(List<List<Long>> characteristicBlocks) {
        return new Query.Builder().bool(b -> b
                .must(characteristicBlocks.stream()
                        .map(ids -> new Query.Builder()
                                .terms(t -> t
                                        .field("characteristicIds")
                                        .terms(tt -> tt
                                                .value(ids.stream().map(FieldValue::of).toList())))
                                .build())
                        .toList()))
                .build();
    }

    public Query createProvidersQuery(List<Long> ids) {
        return new Query.Builder()
                .nested(n -> n
                        .path("providers")
                        .query(b -> b
                                .terms(t -> t
                                        .field("providers.id")
                                        .terms(v -> v.value(ids.stream().map(FieldValue::of).toList())))))
                .build();
    }
}
