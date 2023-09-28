package com.alco.searchapp.utils;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.json.JsonData;
import com.alco.searchapp.dto.Filterable;
import java.util.ArrayList;
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
                .filter(characteristicBlocks.stream()
                        .map(ids -> QueryBuilders
                                .terms(t -> t
                                        .field("characteristicIds")
                                        .terms(tt -> tt
                                                .value(ids.stream().map(FieldValue::of).toList()))))
                        .toList()));
    }

    public Query createProviderPriceRangeQuery(List<Long> ids, Filterable.Price price) {
        ArrayList<Query> queries = new ArrayList<>();
        if (!ids.isEmpty()) {
            queries.add(QueryBuilders.terms(t -> t
                    .field("providers.id")
                    .terms(v -> v.value(ids.stream().map(FieldValue::of).toList()))));
        }

        if (price.min() != null) {
            queries.add(QueryBuilders.range(r -> r
                    .field("providers.price")
                    .gte(JsonData.of(price.min()))));
        }

        if (price.max() != null) {
            queries.add(QueryBuilders.range(r -> r
                    .field("providers.price")
                    .lte(JsonData.of(price.max()))));
        }

        return QueryBuilders.nested(n -> n
                .path("providers")
                .query(q -> q.bool(b -> b.filter(queries))));
    }
}
