package com.alco.searchapp.repository;

import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import com.alco.searchapp.entity.DenormalizedProductEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DenormalizedProductRepository {

    private final ReactiveElasticsearchClient client;

    public Mono<BulkResponse> addList(List<DenormalizedProductEntity> list) {
        BulkRequest.Builder builder = new BulkRequest.Builder();

        list.forEach(entity -> builder.operations(op -> op.index(idx -> idx.document(entity))));
        return client.bulk(builder.build());
    }

    public Mono<DenormalizedProductEntity> save(DenormalizedProductEntity entity) {
        return client.index(idx -> idx.document(entity)
                .index("products"))
                .doOnNext(result -> log.info(result.toString()))
                .then(Mono.just(entity));
    }
}
