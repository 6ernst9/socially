package com.robert.ernst.content.cache;

import com.robert.ernst.content.cache.annotations.CacheableEvict;
import com.robert.ernst.content.cache.annotations.CacheableReactive;
import com.robert.ernst.content.cache.dto.CacheEntry;
import com.robert.ernst.content.dto.Content;
import com.robert.ernst.content.dto.ContentDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class CachingAspect {
    private final Map<String, CacheEntry> storiesCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry> snapsCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry> snapsWithCache = new ConcurrentHashMap<>();
    private final String STORY_KEY = "content:story:";
    private final String SNAPS_KEY = "content:snaps:";

    @Around("@annotation(caching)")
    public Object cache(ProceedingJoinPoint joinPoint, CacheableReactive caching) throws Throwable {
        if(caching.key().equals("story"))
            return cacheStories(joinPoint);
        else if(caching.key().equals("snaps"))
            return cacheSnaps(joinPoint);
        else
            return cacheSnapsWith(joinPoint);
    }

    @Around("@annotation(evict)")
    public Object evict(ProceedingJoinPoint joinPoint, CacheableEvict evict) throws Throwable {
        var result = joinPoint.proceed();
        switch (evict.method()) {
            case "openSnap" -> openEvict(joinPoint, true);
            case "openStory" -> openEvict(joinPoint, false);
            case "addSnap" -> addEvict(joinPoint, true);
            case "addStory" -> addEvict(joinPoint, false);
            case "deleteStory" -> deleteEvict(result);
        }
        return result;
    }

    private void deleteEvict(Object data) {
        var content = (Content)data;

        content.getReceivers()
                .forEach(id -> storiesCache.remove(STORY_KEY + id));
    }

    private void addEvict(ProceedingJoinPoint joinPoint, boolean isSnap) {
        var content = (ContentDto)joinPoint.getArgs()[0];

        if(isSnap){
            snapsWithCache.entrySet().removeIf(entry -> entry.getKey().contains(content.posterId().toString()));
            content.receivers().forEach(id -> {
                snapsCache.remove(SNAPS_KEY + id);
                snapsWithCache.entrySet().removeIf(entry -> entry.getKey().contains(id.toString()));
            });
        }
        else
            content.receivers().forEach(id -> storiesCache.remove(STORY_KEY + id));
    }

    private void openEvict(ProceedingJoinPoint joinPoint, boolean isSnap) {
        var id = (Long)joinPoint.getArgs()[1];

        if(isSnap) {
            snapsCache.remove(SNAPS_KEY + id);
            snapsWithCache.forEach((key, value) -> {
                if (key.contains(id.toString())) {
                    snapsWithCache.remove(key);
                }
            });
        }
        else
            storiesCache.remove(STORY_KEY + id);
    }

    private Object cacheStories(ProceedingJoinPoint joinPoint) throws Throwable {
        var id = (Long)joinPoint.getArgs()[0];
        var key = STORY_KEY + id;

        var entry = storiesCache.get(key);

        if(entry!=null && !entry.isExpired()){
            return Flux.fromIterable((Iterable<?>)entry.getValue());
        } else {
            var result = (Flux<?>)joinPoint.proceed();
            var results = result.toIterable();

            storiesCache.put(key, new CacheEntry(results));
            return result;
        }
    }

    private Object cacheSnaps(ProceedingJoinPoint joinPoint) throws Throwable {
        var id = (Long)joinPoint.getArgs()[0];
        var key = SNAPS_KEY + id;

        var entry = snapsCache.get(key);

        if(entry!=null && !entry.isExpired()){
            return Flux.fromIterable((Iterable<?>)entry.getValue());
        } else {
            var result = (Flux<?>)joinPoint.proceed();
            var snaps = result.toIterable();

            snapsCache.put(key, new CacheEntry(snaps));
            return result;
        }
    }

    private Object cacheSnapsWith(ProceedingJoinPoint joinPoint) throws Throwable {
        var sender = (Long)joinPoint.getArgs()[0];
        var receiver = (Long)joinPoint.getArgs()[1];

        var SNAPS_WITH_KEY = "content:snaps-with:%s:%s";
        var key = SNAPS_WITH_KEY.formatted(sender, receiver);
        var entry = snapsWithCache.get(key);

        if(entry!=null && !entry.isExpired()){
            return Flux.fromIterable((Iterable<?>)entry.getValue());
        } else {
            var result = (Flux<?>)joinPoint.proceed();
            var snaps = result.toIterable();

            snapsWithCache.put(key, new CacheEntry(snaps));
            return result;
        }
    }
}
