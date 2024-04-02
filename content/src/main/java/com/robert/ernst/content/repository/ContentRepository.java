package com.robert.ernst.content.repository;

import com.robert.ernst.content.dto.Content;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ContentRepository extends ReactiveMongoRepository<Content, Long> {
    @Query("{ 'receivers': { '$in': [?0] }, 'type': 'snap' }")
    Flux<Content> getSnapsByUserId(Long userId);

    @Query("{ 'receivers': { '$in': [?0] }, 'type': 'story' }")
    Flux<Content> getStoriesByUserId(Long userId);
}
