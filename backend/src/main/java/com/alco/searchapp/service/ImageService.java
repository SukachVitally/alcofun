package com.alco.searchapp.service;

import com.alco.searchapp.entity.ProductImageEntity;
import com.alco.searchapp.image.FileDownloader;
import com.alco.searchapp.image.StorageInterface;
import com.alco.searchapp.properites.ImageServiceProperty;
import com.alco.searchapp.repository.ProductImageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final FileDownloader downloader;

    private final StorageInterface storage;

    private final ProductImageRepository repository;

    private final ImageServiceProperty property;

    public Mono<ProductImageEntity> addImage(String url, long productId) {
        return downloader.download(url)
                .flatMap(content -> {
                    var entity = new ProductImageEntity();
                    entity.setProductId(productId);
                    entity.setPath(String.format("%s-%s.jpeg", productId, UUID.randomUUID()));
                    return storage.save(property.getBucket(), entity.getPath(), content)
                            .flatMap(res -> repository.save(entity));
                });
    }

    public Flux<String> getProductImageUrls(long productId) {
        return repository.findByProductId(productId).map(this::map);
    }

    public Mono<String> getProductMainImageUrl(long productId) {
        return repository.findOneByProductIdOrderByIdAsc(productId).map(this::map);
    }

    private String map(ProductImageEntity entity) {
        return property.getBaseUrl() + entity.getPath();
    }
}
