package com.robert.ernst.content.service;

import com.robert.ernst.content.cache.annotations.CacheableEvict;
import com.robert.ernst.content.cache.annotations.CacheableReactive;
import com.robert.ernst.content.dto.Content;
import com.robert.ernst.content.dto.ContentDto;
import com.robert.ernst.content.dto.SnapDto;
import com.robert.ernst.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;

    @CacheableReactive(key = "snaps")
    public Flux<SnapDto> getSnaps(Long userId){
        return contentRepository.getSnapsByUserId(userId)
                .map(snap -> new SnapDto(snap.getPosterId(), snap.getId()));
    }

    @CacheableReactive(key = "snap")
    public Mono<Content> openSnap(Long id, Long userId){
        return contentRepository.findById(id)
                .flatMap(snap -> {
                    var seen = snap.getSeen();
                    seen.add(userId);
                    snap.setSeen(seen);
                    return contentRepository.save(snap);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Snap not found")));
    }

    @CacheableEvict(method = "updateStories")
    public Mono<Content> dismissSnap(Long id, Long userId, boolean reload){
        if(reload){
            //TODO handle reloading notification
        }

        return contentRepository.findById(id)
                .flatMap(snap -> {
                    var receivers = snap.getReceivers();
                    receivers.remove(userId);
                    snap.setReceivers(receivers);
                    if(receivers.isEmpty()){
                        return contentRepository.deleteById(id).then(Mono.empty());
                    } else {
                        return contentRepository.save(snap);
                    }
                });
    }

    @CacheableEvict(method = "addSnap")
    public Mono<Content> sendSnap(ContentDto content){
        return contentRepository.save(
                new Content(
                        content.type(),
                        content.url(),
                        content.posterId(),
                        content.receivers()));
    }

    @CacheableEvict(method = "addStory")
    public Mono<Content> addStory(ContentDto content){
        return contentRepository.save(
                new Content(
                        content.type(),
                        content.url(),
                        content.posterId(),
                        content.receivers()));
    }

    @CacheableReactive(key = "stories")
    public Flux<Content> getStories(Long userId){
        return contentRepository.getStoriesByUserId(userId);
    }

    @CacheableEvict(method = "updateStory")
    public Mono<Content> openStory(Long userId, Long id){
        return contentRepository.findById(id)
                .flatMap(story -> {
                    var seen = story.getSeen();
                    seen.add(userId);
                    story.setSeen(seen);
                    return contentRepository.save(story);
                });
    }

    @CacheableEvict(method = "deleteStory")
    public Mono<Void> deleteStory(Long id){
        return contentRepository.deleteById(id);
    }
}
