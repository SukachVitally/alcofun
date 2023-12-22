package com.alco.searchapp.repository.elastic;

import com.alco.searchapp.entity.elastic.ProductSearchEntity;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSearchRepository extends ReactiveElasticsearchRepository<ProductSearchEntity, Long> {
}
