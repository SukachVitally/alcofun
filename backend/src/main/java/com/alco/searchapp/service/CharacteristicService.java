package com.alco.searchapp.service;

import com.alco.searchapp.constants.CharacteristicTypeEnum;
import com.alco.searchapp.dto.CharacteristicDto;
import com.alco.searchapp.dto.CharacteristicTypeDto;
import com.alco.searchapp.entity.CharacteristicEntity;
import com.alco.searchapp.entity.ProductCharacteristicEntity;
import com.alco.searchapp.repository.CharacteristicRepository;
import com.alco.searchapp.repository.ProductCharacteristicRepository;
import com.alco.searchapp.utils.CharacteristicComparator;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CharacteristicService {
    private final ConversionService conversionService;

    private final CharacteristicRepository characteristicRepository;

    private final ProductCharacteristicRepository productCharacteristicRepository;

    public Flux<CharacteristicTypeDto> getTypes() {
        return characteristicRepository.findAll()
                .map(CharacteristicEntity::getTypeId)
                .distinct()
                .map(CharacteristicTypeEnum::findById)
                .map(item -> CharacteristicTypeDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .unitOfMeasurement(item.getUnitOfMeasurement())
                        .build())
                .sort(Comparator.comparing(CharacteristicTypeDto::getName));
    }

    public Mono<Void> add(CharacteristicDto dto, Long productId) {
        return createCharacteristicIfNotExist(dto.getType(), dto.getValue().toString())
                .flatMap(characteristicEntity -> {
                    var entity = new ProductCharacteristicEntity();
                    entity.setCharacteristicId(characteristicEntity.getId());
                    entity.setProductId(productId);
                    entity.setCreatedAt(LocalDateTime.now());

                    return productCharacteristicRepository.save(entity)
                            .onErrorResume(DuplicateKeyException.class, e -> Mono.empty());
                })
                .then();
    }

    public Flux<CharacteristicDto> getAllForProduct(Long productId) {
        return productCharacteristicRepository.findAllByProductId(productId)
                .flatMap(productCharacteristic -> characteristicRepository.findById(productCharacteristic.getCharacteristicId())
                        .map(this::map));
    }

    public Flux<CharacteristicDto> getAllForType(Integer typeId) {
        return characteristicRepository.findAllByTypeId(typeId)
                .map(this::map)
                .collectSortedList(new CharacteristicComparator())
                .flatMapMany(Flux::fromIterable);
    }

    public Mono<List<List<Long>>> spitByTypes(List<Long> ids){
        return characteristicRepository.findAllByIds(ids)
                .groupBy(CharacteristicEntity::getTypeId)
                .flatMap(group -> group.map(CharacteristicEntity::getId).collectList())
                .collectList();
    }

    private CharacteristicDto map(CharacteristicEntity entity) {
        var type = CharacteristicTypeEnum.findById(entity.getTypeId());
        return CharacteristicDto.builder()
                .id(entity.getId())
                .type(type)
                .value(conversionService.convert(entity.getValue(), type.getType()))
                .build();
    }

    private Mono<CharacteristicEntity> createCharacteristicIfNotExist(CharacteristicTypeEnum type, String value) {
        return characteristicRepository.findOneByTypeIdAndValue(type.getId(), value)
                .switchIfEmpty(createCharacteristic(type, value));
    }

    private Mono<CharacteristicEntity> createCharacteristic(CharacteristicTypeEnum type, String value) {
        var entity = new CharacteristicEntity();
        entity.setTypeId(type.getId());
        entity.setValue(value);
        entity.setCreatedAt(LocalDateTime.now());
        return characteristicRepository.save(entity)
                .onErrorResume(DuplicateKeyException.class, e -> characteristicRepository.findOneByTypeIdAndValue(type.getId(), value));
    }

}
