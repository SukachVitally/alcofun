package com.alco.searchapp.repository;

import com.alco.searchapp.entity.CategoryEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface CategoryRepository extends R2dbcRepository<CategoryEntity, Long> {
    Mono<CategoryEntity> findOneByTypeId(Integer typeId);
}
