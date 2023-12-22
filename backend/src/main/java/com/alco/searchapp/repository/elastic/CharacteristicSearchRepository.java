package com.alco.searchapp.repository.elastic;

import com.alco.searchapp.entity.elastic.CharacteristicSearchEntity;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CharacteristicSearchRepository extends ReactiveElasticsearchRepository<CharacteristicSearchEntity, Long> {
    Flux<CharacteristicSearchEntity> findAllByTypeId(Integer typeId);
}
