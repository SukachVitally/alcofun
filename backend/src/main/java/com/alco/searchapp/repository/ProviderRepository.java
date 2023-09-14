package com.alco.searchapp.repository;

import com.alco.searchapp.entity.ProviderEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ProviderRepository extends R2dbcRepository<ProviderEntity, Long> {
    Mono<ProviderEntity> findOneByTypeId(Integer typeId);
}
