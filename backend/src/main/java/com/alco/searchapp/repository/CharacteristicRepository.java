package com.alco.searchapp.repository;

import com.alco.searchapp.entity.CharacteristicEntity;
import java.util.List;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CharacteristicRepository extends R2dbcRepository<CharacteristicEntity, Long> {
    Mono<CharacteristicEntity> findOneByTypeIdAndValue(int typeId, String value);

    Flux<CharacteristicEntity> findAllByTypeId(int typeId);

    @Query("SELECT * FROM characteristic WHERE id in (:ids)")
    Flux<CharacteristicEntity> findAllByIds(List<Long> ids);
}
