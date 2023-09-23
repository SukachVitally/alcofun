package com.alco.searchapp.repository;

import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.util.ObjectBuilder;
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
    private final String INDEX = "products";

    private final ReactiveElasticsearchClient client;

    public Mono<Boolean> clean() {
        return client.indices()
                .delete(new DeleteIndexRequest.Builder().index(List.of(INDEX)).build())
                .log()
                .onErrorReturn(new DeleteIndexResponse.Builder().acknowledged(false).build())
                .then(Mono.just(true));
    }

    public Mono<BulkResponse> addList(List<DenormalizedProductEntity> list) {
        BulkRequest.Builder builder = new BulkRequest.Builder();

        list.forEach(entity -> builder.operations(op ->
                op.index(idx -> idx
                        .index(INDEX).document(entity))));
        return client.bulk(builder.build())
                .log();
    }

    public Mono<DenormalizedProductEntity> save(DenormalizedProductEntity entity) {
        return client.index(idx -> idx.document(entity)
                .index(INDEX))
                .doOnNext(result -> log.info(result.toString()))
                .then(Mono.just(entity));
    }
}
