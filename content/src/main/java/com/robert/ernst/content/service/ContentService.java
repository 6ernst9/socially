package com.robert.ernst.content.service;

import com.robert.ernst.content.cache.annotations.CacheableEvict;
import com.robert.ernst.content.cache.annotations.CacheableReactive;
import com.robert.ernst.content.dto.Content;
import com.robert.ernst.content.dto.ContentDto;
import com.robert.ernst.content.dto.RequestContext;
import com.robert.ernst.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {
    private final ContentRepository contentRepository;
    private final WebClient webClient;
    @Value("${connection.base-url}")
    private String connectionBaseUrl;
    @Value("${security.client-id}")
    private String clientId;
    @Value("${security.client-secret}")
    private String clientSecret;

    private final Sort sort = Sort.by(Sort.Direction.ASC, "timestamp");

    @CacheableReactive(key = "snaps")
    public Flux<Content> getSnaps(Long id){
        return contentRepository.getSnapsByUserId(id)
                .map(snap -> snap.getPosterId().equals(id)
                        ? id : snap.getPosterId())
                .collect(Collectors.toSet())
                .flux()
                .flatMap(Flux::fromIterable)
                .flatMap(partnerId -> contentRepository.getSnapsBetween(id, partnerId, sort).last());
    }

    @CacheableReactive(key = "snapsWith")
    public Flux<Content> getSnapsWith(Long sender, Long receiver){
        return contentRepository.getSnapsBetween(sender, receiver, sort);
    }

    @CacheableEvict(method = "openSnap")
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

    @CacheableEvict(method = "openSnap")
    public Mono<Content> dismissSnap(Long id, Long userId, boolean reload){
        if(reload){
            //TODO handle reloading notification
        }

        return contentRepository.findById(id)
                .flatMap(snap -> {
                    var receivers = snap.getReceivers();
                    receivers.remove(userId);
                    snap.setReceivers(receivers);
                    return contentRepository.save(snap);
                });
    }

    @CacheableEvict(method = "addSnap")
    public Mono<Content> sendSnap(ContentDto snap){
        var content = contentRepository.save(
                new Content(
                        snap.type(),
                        snap.url(),
                        snap.posterId(),
                        snap.receivers()));

        snap.receivers().forEach(receiver -> updateStreak(snap.posterId(), receiver));
        return content;
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

    @CacheableReactive(key = "story")
    public Flux<Content> getStories(Long userId){
        return contentRepository.getStoriesByUserId(userId);
    }

    @CacheableEvict(method = "openStory")
    public Mono<Content> openStory(Long id,Long userId){
        return contentRepository.findById(id)
                .flatMap(story -> {
                    var seen = story.getSeen();
                    seen.add(userId);
                    story.setSeen(seen);
                    return contentRepository.save(story);
                });
    }

    @CacheableEvict(method = "deleteStory")
    public Mono<Content> deleteStory(Long id){
        var story = contentRepository.findById(id);
        return contentRepository.deleteById(id).then(story);
    }

    private void updateStreak(Long sender, Long receiver) {
        var todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        var todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        var snapsFromSender = contentRepository.findSnapsBetweenUsersOnSameDay(sender, receiver, todayStart, todayEnd);
        var snapsFromReceiver = contentRepository.findSnapsBetweenUsersOnSameDay(receiver, sender, todayStart, todayEnd);

        Flux.merge(snapsFromSender, snapsFromReceiver)
                .collectList()
                .doOnNext(list -> {
                    if(!list.isEmpty()){
                        makeRequest(sender, receiver);
                    }
                })
                .subscribe();
    }

    private void makeRequest(Long sender, Long receiver) {
        var headers = RequestContext.getHeaders();

        var webRequest = webClient.post()
                .uri(connectionBaseUrl.formatted(sender, receiver))
                .headers(httpHeaders -> headers.forEach(httpHeaders::set))
                .header("clientId",clientId)
                .header("clientSecret", clientSecret)
                .header("X-FI-SY-PATH", "connection.update-snapstreak")
                .accept(MediaType.APPLICATION_JSON);

        webRequest.exchangeToMono(response -> {
            if (response.statusCode() == HttpStatus.OK) {
                log.info("Snapstreak updated successfully for users: {}, {}", sender, receiver);
                return Mono.empty();
            } else {
                return response.bodyToMono(Object.class)
                        .flatMap(err -> {
                            log.error("Request failed: {}", err);
                            return Mono.error(new RuntimeException("Request failed:" + err));
                        });
            }
        }).subscribe();
    }
}
