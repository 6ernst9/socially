package com.robert.ernst.content.controller;

import com.robert.ernst.content.config.annotations.WmtSecurityMapping;
import com.robert.ernst.content.dto.Content;
import com.robert.ernst.content.dto.ContentDto;
import com.robert.ernst.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ContentController {
    private final ContentService contentService;

    @GetMapping("get-snaps/{userId}")
    @WmtSecurityMapping(path = "content.get-snaps")
    public Flux<Content> getSnaps(@PathVariable Long userId){
        log.info("Received GET snaps request for userId: {}", userId);
        return contentService.getSnaps(userId);
    }

    @GetMapping("get-snaps-with/{sender}/{receiver}")
    @WmtSecurityMapping(path = "content.get-snaps-with")
    public Flux<Content> getSnapsWith(@PathVariable Long sender,
                                      @PathVariable Long receiver){
        log.info("Received GET snaps request between userId: {} and userId: {}", sender, receiver);
        return contentService.getSnapsWith(sender, receiver);
    }

    @GetMapping("open-snap/{userId}/{id}")
    @WmtSecurityMapping(path = "content.open-snap")
    public Mono<Content> openSnap(@PathVariable Long userId,
                                  @PathVariable String id){
        log.info("Received GET (reload snap) request by userId: {}, for snap id: {}", userId, id);
        return contentService.openSnap(id, userId);
    }

    @GetMapping("get-stories/{userId}")
    @WmtSecurityMapping(path = "content.get-stories")
    public Flux<Content> getStories(@PathVariable Long userId){
        log.info("Received GET stories request for userId: {}", userId);
        return contentService.getStories(userId);
    }

    @PostMapping("open-story/{userId}/{id}")
    @WmtSecurityMapping(path = "content.open-story")
    public Mono<Void> openStory(@PathVariable Long userId, @PathVariable String id){
        log.info("Received POST (open story) request for userId: {}, and story id: {}", userId, id);
        return contentService.openStory(id, userId)
                .then(Mono.empty());
    }

    @PostMapping("send-snap")
    @WmtSecurityMapping(path = "content.send-snap")
    public Mono<Void> sendSnap(@RequestBody ContentDto content){
        log.info("Received POST (send snap) request with content: {}", content);
        return contentService.sendSnap(content)
                .then(Mono.empty());
    }

    @PostMapping("add-story")
    @WmtSecurityMapping(path = "content.add-story")
    public Mono<Void> addStory(@RequestBody ContentDto content){
        log.info("Received POST (add story) request with content: {}", content);
        return contentService.addStory(content).then(Mono.empty());
    }

    @DeleteMapping("delete-story/{id}")
    @WmtSecurityMapping(path = "content.delete-story")
    public Mono<String> deleteStory(@PathVariable String id){
        log.info("Received DELETE (delete story) request for story id: {}", id);
        return contentService.deleteStory(id)
                .then(Mono.just("Story deleted successfully"));
    }
}
