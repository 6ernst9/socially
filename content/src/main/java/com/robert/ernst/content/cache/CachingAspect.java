package com.robert.ernst.content.cache;

import com.robert.ernst.content.cache.annotations.CacheableEvict;
import com.robert.ernst.content.cache.annotations.CacheableReactive;
import com.robert.ernst.content.cache.dto.CacheEntry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class CachingAspect {
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final String STORY_KEY = "story:";
    private final String SNAP_KEY = "snap:";
    private final String SNAPS_KEY = "snaps:";

    @Around("@annotation(caching)")
    public Object cache(ProceedingJoinPoint joinPoint, CacheableReactive caching) throws Throwable {
        if(caching.key().equals("story")){
            return cacheStories(joinPoint);
        } else if(caching.key().equals("snap")){
            return cacheSnap(joinPoint);
        } else {
            return cacheSnaps(joinPoint);
        }
    }

    @Around("@annotation(evict)")
    public Object evict(ProceedingJoinPoint joinPoint, CacheableEvict evict) throws Throwable {

        return joinPoint.proceed();
    }

    private Object cacheStories(ProceedingJoinPoint joinPoint) throws Throwable {
        var id = (Long)joinPoint.getArgs()[0];
        var key = STORY_KEY + id;

        var entry = cache.get(key);

        if(entry!=null && !entry.isExpired()){
            return Flux.fromIterable((Iterable<?>)entry.getValue());
        } else {
            var result = (Flux<?>)joinPoint.proceed();
            var results = result.toIterable();

            cache.put(key, new CacheEntry(results));
            return result;
        }
    }

    private Object cacheSnap(ProceedingJoinPoint joinPoint) throws Throwable {
        var id = (Long)joinPoint.getArgs()[0];
        var key = SNAP_KEY + id;

        var entry = cache.get(key);

        if(entry!=null && !entry.isExpired()){
            return Mono.just(entry.getValue());
        } else {
            var result = (Mono<?>)joinPoint.proceed();
            var account = result.block();

            cache.put(key, new CacheEntry(account));
            return result;
        }
    }

    private Object cacheSnaps(ProceedingJoinPoint joinPoint) throws Throwable {
        var id = (Long)joinPoint.getArgs()[0];
        var key = SNAPS_KEY + id;

        var entry = cache.get(key);

        if(entry!=null && !entry.isExpired()){
            return Flux.fromIterable((Iterable<?>)entry.getValue());
        } else {
            var result = (Flux<?>)joinPoint.proceed();
            var snaps = result.toIterable();

            cache.put(key, new CacheEntry(snaps));
            return result;
        }
    }
}
