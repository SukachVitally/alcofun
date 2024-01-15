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
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;

@UtilityClass
public class ProductQueryUtils {
    public NativeQuery createQuery(Filterable filterable, List<List<Long>> characteristicBlocks) {
        NativeQueryBuilder builder = new NativeQueryBuilder();
        ArrayList<Query> queries = new ArrayList<>();
        queries.add(QueryBuilders.exists(b -> b.field("price")));

        if (!filterable.getCategories().isEmpty()) {
            queries.add(ProductQueryUtils.createCategoriesQuery(filterable.getCategories()));
        }

        if (filterable.getSearch() != null) {
            queries.add(ProductQueryUtils.createSearchQuery(filterable.getSearch()));
        }

        if (!characteristicBlocks.isEmpty()) {
            queries.add(ProductQueryUtils.createCharacteristicsQuery(characteristicBlocks));
        }

        if (!filterable.getProviders().isEmpty()) {
            queries.add(ProductQueryUtils.createProviderQuery(filterable.getProviders()));
        }

        if (filterable.getPrice().min() != null) {
            queries.add(ProductQueryUtils.createMinPriceQuery(filterable.getPrice().min()));
        }

        if (filterable.getPrice().max() != null) {
            queries.add(ProductQueryUtils.createMaxPriceQuery(filterable.getPrice().max()));
        }

        if (filterable.getHot()) {
            queries.add(ProductQueryUtils.createHotQuery());
        }

        return new NativeQuery(builder.withFilter(QueryBuilders.bool(b -> b.filter(queries))));
    }

    private Query createCategoriesQuery(List<Long> ids) {
        var values = ids.stream().map(FieldValue::of).toList();
        return QueryBuilders.terms(t -> t
                        .field("categoryId")
                        .terms(tt -> tt.value(values)));
    }

    private Query createSearchQuery(String search) {
        return QueryBuilders.bool(b -> b
                .should(List.of(
                    QueryBuilders.term(m -> m.field("ean").value(search)),
                    QueryBuilders.match(p -> p.field("name").query(search).operator(Operator.And))
                )
            ));
    }

    private Query createCharacteristicsQuery(List<List<Long>> characteristicBlocks) {
        return QueryBuilders.bool(b -> b
                .filter(characteristicBlocks.stream()
                        .map(ids -> QueryBuilders
                                .terms(t -> t
                                        .field("characteristics.id")
                                        .terms(tt -> tt
                                                .value(ids.stream().map(FieldValue::of).toList()))))
                        .toList()));
    }



    private Query createProviderQuery(List<Long> ids) {
        return QueryBuilders.terms(t -> t
                    .field("providers.id")
                    .terms(v -> v.value(ids.stream().map(FieldValue::of).toList())));
    }

    private Query createMinPriceQuery(Double minPrice) {
        return QueryBuilders.range(r -> r
                    .field("price")
                    .gte(JsonData.of(minPrice)));
    }

    private Query createMaxPriceQuery(Double maxPrice) {
        return QueryBuilders.range(r -> r
                .field("price")
                .lte(JsonData.of(maxPrice)));
    }

    private Query createHotQuery() {
        return QueryBuilders.term(t -> t
                    .field("hot")
                    .value(true));
    }
}
