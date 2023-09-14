package com.alco.searchapp.service;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.dto.CategoryDto;
import com.alco.searchapp.entity.CategoryEntity;
import com.alco.searchapp.exception.CategoryNotFound;
import com.alco.searchapp.repository.CategoryRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Mono<CategoryDto> createCategoryIfNotExist(CategoryTypeEnum category) {
        return categoryRepository.findOneByTypeId(category.getId())
                .switchIfEmpty(createCategory(category.getId()))
                .map(this::map);
    }

    public Flux<CategoryDto> getCategories() {
        return categoryRepository.findAll()
                .map(this::map)
                .sort(Comparator.comparing(categoryDto -> categoryDto.getType().getName()));
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
}
