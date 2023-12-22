package com.alco.searchapp.service;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.dto.CategoryDto;
import com.alco.searchapp.entity.CategoryEntity;
import com.alco.searchapp.entity.elastic.CategorySearchEntity;
import com.alco.searchapp.exception.CategoryNotFound;
import com.alco.searchapp.repository.CategoryRepository;
import com.alco.searchapp.repository.elastic.CategorySearchRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    private final CategorySearchRepository searchRepository;

    public Mono<List<Long>> refresh(List<Long> ids) {
        return searchRepository
                .deleteAll()
                .thenMany(Flux.fromIterable(ids))
                .flatMap(categoryRepository::findById)
                .map(category -> {
                    var entity = new CategorySearchEntity();
                    entity.setId(category.getId());
                    entity.setTypeId(category.getTypeId());
                    return entity;
                })
                .flatMap(searchRepository::save)
                .then(Mono.just(ids));
    }

    public Mono<CategoryDto> createCategoryIfNotExist(CategoryTypeEnum category) {
        return categoryRepository.findOneByTypeId(category.getId())
                .switchIfEmpty(createCategory(category.getId()))
                .map(this::map);
    }

    public Flux<CategoryDto> getAvailableCategories() {
        return searchRepository.findAll()
                .map(this::map);
    }

    public Mono<CategoryDto> getCategory(Long id) {
        return categoryRepository.findById(id)
                .switchIfEmpty(Mono.error(new CategoryNotFound(String.format("Category id %s not found", id), null)))
                .map(this::map);
    }

    private Mono<CategoryEntity> createCategory(Integer typeId) {
        var entity = new CategoryEntity();
        entity.setTypeId(typeId);
        entity.setCreatedAt(LocalDateTime.now());
        return categoryRepository.save(entity)
                .onErrorResume(DuplicateKeyException.class, e -> categoryRepository.findOneByTypeId(typeId));
    }

    private CategoryDto map(CategoryEntity entity) {
        return CategoryDto.builder()
                .id(entity.getId())
                .type(CategoryTypeEnum.findById(entity.getTypeId()))
                .build();
    }

    private CategoryDto map(CategorySearchEntity entity) {
        return CategoryDto.builder()
                .id(entity.getId())
                .type(CategoryTypeEnum.findById(entity.getTypeId()))
                .build();
    }
}
