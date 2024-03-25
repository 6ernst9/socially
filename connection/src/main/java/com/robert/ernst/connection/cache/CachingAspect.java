package com.robert.ernst.connection.cache;

import com.robert.ernst.connection.cache.annotations.CacheableEvict;
import com.robert.ernst.connection.cache.annotations.CacheableReactive;
import com.robert.ernst.connection.cache.dto.CacheEntry;
import com.robert.ernst.connection.cache.dto.CacheKey;
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
    private final Map<Long, CacheEntry> requestsCache = new ConcurrentHashMap<>();
    private final Map<Long, CacheEntry> friendsCache = new ConcurrentHashMap<>();
    private final Map<Long, CacheEntry> blockedCache = new ConcurrentHashMap<>();
    private final Map<CacheKey, CacheEntry> connectionCache = new ConcurrentHashMap<>();

    @Around("@annotation(caching)")
    public Object cache(ProceedingJoinPoint joinPoint, CacheableReactive caching) throws Throwable {
        return switch (caching.key()) {
            case "requests" -> cacheId(joinPoint, requestsCache);
            case "friends" -> cacheId(joinPoint, friendsCache);
            case "blocked" -> cacheId(joinPoint, blockedCache);
            case "connection" -> cacheConnection(joinPoint);
        };
    }

    @Around("@annotation(evict)")
    public Object evict(ProceedingJoinPoint joinPoint, CacheableEvict evict) throws Throwable {
        switch (evict.method()) {
            case "accepted" -> acceptEvict(joinPoint);
            case "requested" -> requestEvict(joinPoint);
            case "removed" -> removeEvict(joinPoint);
            case "blocked" -> blockEvict(joinPoint);
        }
        return joinPoint.proceed();
    }

    private Object cacheId(ProceedingJoinPoint joinPoint, Map<Long, CacheEntry> cache) throws Throwable {
        var id = (Long)joinPoint.getArgs()[0];
        var entry = cache.get(id);

        if(entry!=null && !entry.isExpired()){
            return Flux.fromIterable((Iterable<?>)entry.getValue());
        } else {
            var result = (Flux<?>)joinPoint.proceed();
            var results = result.toIterable();

            cache.put(id, new CacheEntry(results));
            return result;
        }
    }

    private Object cacheConnection(ProceedingJoinPoint joinPoint) throws Throwable {
        var id = (Long)joinPoint.getArgs()[0];
        var userId = (Long)joinPoint.getArgs()[1];

        var key = new CacheKey(id, userId);
        var entry = connectionCache.get(new CacheKey(id, userId));

        if(entry!=null && !entry.isExpired()){
            return Mono.just(entry.getValue());
        } else {
            var result = (Mono<?>)joinPoint.proceed();
            var account = result.block();

            connectionCache.put(key, new CacheEntry(account));
            return result;
        }
    }



    private void acceptEvict(ProceedingJoinPoint joinPoint){
        Long id = (Long)joinPoint.getArgs()[0];
        Long userId = (Long)joinPoint.getArgs()[1];

        friendsCache.remove(id);
        friendsCache.remove(userId);
        requestsCache.remove(id);

        connectionCache.remove(new CacheKey(id, userId));
        connectionCache.remove(new CacheKey(userId, id));
    }

    private void requestEvict(ProceedingJoinPoint joinPoint) {
        Long id = (Long)joinPoint.getArgs()[0];
        Long userId = (Long)joinPoint.getArgs()[1];

        requestsCache.remove(userId);
        connectionCache.remove(new CacheKey(id, userId));
        connectionCache.remove(new CacheKey(userId, id));
    }

    private void blockEvict(ProceedingJoinPoint joinPoint) {
        Long id = (Long)joinPoint.getArgs()[0];
        Long userId = (Long)joinPoint.getArgs()[1];

        friendsCache.remove(id);
        friendsCache.remove(userId);

        blockedCache.remove(id);

        connectionCache.remove(new CacheKey(id, userId));
        connectionCache.remove(new CacheKey(userId, id));
    }

    private void removeEvict(ProceedingJoinPoint joinPoint){
        Long id = (Long)joinPoint.getArgs()[0];
        Long userId = (Long)joinPoint.getArgs()[1];

        friendsCache.remove(id);
        friendsCache.remove(userId);

        connectionCache.remove(new CacheKey(id, userId));
        connectionCache.remove(new CacheKey(userId, id));
    }
}
