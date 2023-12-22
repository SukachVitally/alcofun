package com.alco.searchapp.repository.elastic;

import com.alco.searchapp.entity.elastic.CategorySearchEntity;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorySearchRepository extends ReactiveElasticsearchRepository<CategorySearchEntity, Long> {
}
