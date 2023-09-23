package com.alco.searchapp.repository;

import com.alco.searchapp.entity.DenormalizedProductEntity;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DenormalizedProductRepository extends ReactiveElasticsearchRepository<DenormalizedProductEntity, Long> {
}
