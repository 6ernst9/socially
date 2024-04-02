package com.robert.ernst.content.controller;

import com.robert.ernst.content.config.annotations.WmtSecurityMapping;
import com.robert.ernst.content.dto.Content;
import com.robert.ernst.content.dto.ContentDto;
import com.robert.ernst.content.dto.SnapDto;
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
    public Flux<SnapDto> getSnaps(@PathVariable Long userId){
        log.info("Received GET snaps request for userId: {}", userId);
        return contentService.getSnaps(userId);
    }

    @GetMapping("open-snap/{userId}/{id}")
    @WmtSecurityMapping(path = "content.open-snap")
    public Mono<Content> openSnap(@PathVariable Long userId,
                                  @PathVariable Long id){
        log.info("Received GET (reload snap) request by userId: {}, for snap id: {}", userId, id);
        return contentService.openSnap(id, userId);
    }

    @GetMapping("reload-snap/{userId}/{id}")
    @WmtSecurityMapping(path = "content.reload-snap")
    public Mono<Content> reloadSnap(@PathVariable Long userId,
                                    @PathVariable Long id){
        log.info("Received GET (reload snap) request by userId: {}, for snap id: {}", userId, id);
        return contentService.openSnap(id, userId)
                .flatMap(snap -> contentService.dismissSnap(id, userId, true).then(Mono.just(snap)));
    }

    @GetMapping("dismiss-snap/{userId}/{id}")
    @WmtSecurityMapping(path = "content.dismiss-snap")
    public Mono<Void> dismissSnap(@PathVariable Long userId,
                                  @PathVariable Long id){
        log.info("Received GET (dismiss snap) request for snap id: {}", id);
        return contentService.dismissSnap(id, userId,false).then(Mono.empty());
    }

    @GetMapping("get-stories/{userId}")
    @WmtSecurityMapping(path = "content.get-stories")
    public Flux<Content> getStories(@PathVariable Long userId){
        log.info("Received GET stories request for userId: {}", userId);
        return contentService.getStories(userId);
    }

    @PostMapping("open-story/{userId}/{id}")
    @WmtSecurityMapping(path = "content.open-story")
    public Mono<Void> openStory(@PathVariable Long userId, @PathVariable Long id){
        log.info("Received POST (open story) request for userId: {}, and story id: {}", userId, id);
        return contentService.openStory(userId, id)
                .then(Mono.empty());
    }

    @PostMapping("send-snap")
    @WmtSecurityMapping(path = "content.send-snap")
    public Mono<String> sendSnap(@RequestBody ContentDto content){
        log.info("Received POST (send snap) request with content: {}", content);
        return contentService.sendSnap(content)
                .flatMap(snap -> {
                    //TODO handle notification;
                    return Mono.just("Snap sent successfully");
                });
    }

    @PostMapping("add-story")
    @WmtSecurityMapping(path = "content.add-story")
    public Mono<String> addStory(@RequestBody ContentDto content){
        log.info("Received POST (add story) request with content: {}", content);
        return contentService.addStory(content)
                .then(Mono.just("Story added successfully"));
    }

    @DeleteMapping("delete-story/{id}")
    @WmtSecurityMapping(path = "content.delete-story")
    public Mono<String> deleteStory(@PathVariable Long id){
        log.info("Received DELETE (delete story) request for story id: {}", id);
        return contentService.deleteStory(id)
                .then(Mono.just("Story deleted successfully"));
    }
}
