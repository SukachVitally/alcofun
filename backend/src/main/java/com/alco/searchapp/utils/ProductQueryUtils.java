package com.alco.searchapp.utils;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProductQueryUtils {
    public Query createCategoriesQuery(List<Long> ids) {
        var values = ids.stream().map(FieldValue::of).toList();
        return QueryBuilders.terms(t -> t
                        .field("categoryId")
                        .terms(tt -> tt.value(values)));
    }

    public Query createSearchQuery(String search) {
        return QueryBuilders.bool(b -> b
                .should(List.of(
                    QueryBuilders.term(m -> m.field("ean").value(search)),
                    QueryBuilders.match(p -> p.field("name").query(search).operator(Operator.And))
                )
            ));
    }

    public Query createCharacteristicsQuery(List<List<Long>> characteristicBlocks) {
        return QueryBuilders.bool(b -> b
                .must(characteristicBlocks.stream()
                        .map(ids -> QueryBuilders
                                .terms(t -> t
                                        .field("characteristicIds")
                                        .terms(tt -> tt
                                                .value(ids.stream().map(FieldValue::of).toList()))))
                        .toList()));
    }

    public Query createProvidersQuery(List<Long> ids) {
        return QueryBuilders
                .nested(n -> n
                        .path("providers")
                        .query(b -> b
                                .terms(t -> t
                                        .field("providers.id")
                                        .terms(v -> v.value(ids.stream().map(FieldValue::of).toList())))));
    }
}
