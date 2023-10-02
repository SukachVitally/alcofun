package com.alco.searchapp.repository;

import com.alco.searchapp.entity.ProductRatingEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ProductRatingRepository extends R2dbcRepository<ProductRatingEntity, Long> {
    @Query("SELECT DISTINCT ON (pr.provider_id) * FROM product_rating pr WHERE pr.product_id = :productId " +
            "ORDER BY pr.provider_id, pr.id DESC")
    Flux<ProductRatingEntity> findLatestRatings(Long productId);
}
