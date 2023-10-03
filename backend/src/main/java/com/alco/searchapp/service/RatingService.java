package com.alco.searchapp.service;

import com.alco.searchapp.dto.RatingDto;
import com.alco.searchapp.entity.ProductRatingEntity;
import com.alco.searchapp.repository.ProductRatingRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final ProductRatingRepository repository;

    public Mono<Void> addRating(Long productId, Long providerId, RatingDto rating) {
        var entity = new ProductRatingEntity();
        entity.setProductId(productId);
        entity.setRating(rating.rating());
        entity.setVoteCount(rating.votes());
        entity.setProviderId(providerId);
        entity.setCreatedAt(LocalDateTime.now());

        return repository.save(entity).then();
    }

    public Mono<RatingDto> getCompositeRating(Long productId) {
        return repository.findLatestRatings(productId).reduce(new RatingDto(0f, 0), (accum, entity) -> {
            var votes = accum.votes() + entity.getVoteCount();
            var compositeRating = (accum.rating() * accum.votes() + entity.getRating() * entity.getVoteCount())
                    / votes;
            return new RatingDto(compositeRating, votes);
        });
    }

    public Float getInternalRating(RatingDto compositeRating) {
        var coefficient = (float) (compositeRating.rating() < 3 ? - 0.01 : 0.01);
        return compositeRating.rating() + coefficient * compositeRating.rating() * compositeRating.votes();
    }

    // todo: Add formula
    public Mono<Float> getInternalRating(Long productId) {
        return getCompositeRating(productId).map(this::getInternalRating);
    }
}
