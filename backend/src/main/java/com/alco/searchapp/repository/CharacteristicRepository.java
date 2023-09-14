package com.alco.searchapp.repository;

import com.alco.searchapp.entity.CharacteristicEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CharacteristicRepository extends R2dbcRepository<CharacteristicEntity, Long> {
    Mono<CharacteristicEntity> findOneByTypeIdAndValue(int typeId, String value);

    Flux<CharacteristicEntity> findAllByTypeId(int typeId);
}
