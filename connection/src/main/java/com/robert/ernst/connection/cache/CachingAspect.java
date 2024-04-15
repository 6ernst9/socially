package com.robert.ernst.connection.cache;

import com.robert.ernst.connection.cache.annotations.CacheableEvict;
import com.robert.ernst.connection.cache.annotations.CacheableReactive;
import com.robert.ernst.connection.cache.dto.CacheEntry;
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
    private final Map<String, CacheEntry> friendsCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry> blockedCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry> requestsCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry> connectionCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry> snapstreakCache = new ConcurrentHashMap<>();
    private final String FRIENDS_KEY = "connection:friends:";
    private final String BLOCKED_KEY = "connection:blocked:";
    private final String REQUESTS_KEY = "connection:requests:";
    private final String CONNECTION_KEY = "connection:connection:%s:%s";
    private final String SNAPSTREAK_KEY = "connection:snapstreak:%s:%s";

    @Around("@annotation(caching)")
    public Object cache(ProceedingJoinPoint joinPoint, CacheableReactive caching) throws Throwable {
        return switch (caching.key()) {
            case "requests" -> cache(joinPoint, requestsCache, REQUESTS_KEY);
            case "friends" -> cache(joinPoint, friendsCache, FRIENDS_KEY);
            case "blocked" -> cache(joinPoint, blockedCache, BLOCKED_KEY);
            case "connection" -> cacheConnection(joinPoint, connectionCache, CONNECTION_KEY);
            case "snapstreak" -> cacheConnection(joinPoint, snapstreakCache, SNAPSTREAK_KEY);
            default -> null;
        };
    }

    @Around("@annotation(evict)")
    public Object evict(ProceedingJoinPoint joinPoint, CacheableEvict evict) throws Throwable {
        switch (evict.method()) {
            case "accepted" -> acceptEvict(joinPoint);
            case "requested" -> requestEvict(joinPoint);
            case "removed" -> removeEvict(joinPoint);
            case "blocked" -> blockEvict(joinPoint);
            case "unblocked" -> unblockEvict(joinPoint);
            case "updated" -> snapstreakEvict(joinPoint);
        }
        return joinPoint.proceed();
    }

    private Object cache(ProceedingJoinPoint joinPoint, Map<String, CacheEntry> cache, String key) throws Throwable {
        var id = (Long)joinPoint.getArgs()[0];
        var set = key + id;

        var entry = cache.get(set);

        if(entry!=null && !entry.isExpired()){
            return Flux.fromIterable((Iterable<?>)entry.getValue());
        } else {
            var result = (Flux<?>)joinPoint.proceed();
            var results = result.toIterable();

            cache.put(set, new CacheEntry(results));
            return result;
        }
    }

    private Object cacheConnection(ProceedingJoinPoint joinPoint, Map<String, CacheEntry> cache, String key) throws Throwable {
        var id = (Long)joinPoint.getArgs()[0];
        var userId = (Long)joinPoint.getArgs()[1];

        var set = key.formatted(id, userId);
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

    private void acceptEvict(ProceedingJoinPoint joinPoint){
        var id = (Long)joinPoint.getArgs()[0];
        var userId = (Long)joinPoint.getArgs()[1];

        friendsCache.remove(FRIENDS_KEY + id);
        friendsCache.remove(FRIENDS_KEY + userId);

        requestsCache.remove(REQUESTS_KEY + userId);

        connectionCache.remove(CONNECTION_KEY.formatted(id, userId));
        connectionCache.remove(CONNECTION_KEY.formatted(userId, id));
    }

    private void requestEvict(ProceedingJoinPoint joinPoint) {
        var id = (Long)joinPoint.getArgs()[0];
        var userId = (Long)joinPoint.getArgs()[1];

        requestsCache.remove(REQUESTS_KEY + userId);

        connectionCache.remove(CONNECTION_KEY.formatted(id, userId));
        connectionCache.remove(CONNECTION_KEY.formatted(userId, id));
    }

    private void blockEvict(ProceedingJoinPoint joinPoint) {
        var id = (Long) joinPoint.getArgs()[0];
        var userId = (Long) joinPoint.getArgs()[1];

        friendsCache.remove(FRIENDS_KEY + id);
        friendsCache.remove(FRIENDS_KEY + userId);

        requestsCache.remove(BLOCKED_KEY + id);

        connectionCache.remove(CONNECTION_KEY.formatted(id, userId));
        connectionCache.remove(CONNECTION_KEY.formatted(userId, id));
    }

    private void unblockEvict(ProceedingJoinPoint joinPoint) {
        var id = (Long) joinPoint.getArgs()[0];
        var userId = (Long) joinPoint.getArgs()[1];

        requestsCache.remove(BLOCKED_KEY + id);

        connectionCache.remove(CONNECTION_KEY.formatted(id, userId));
        connectionCache.remove(CONNECTION_KEY.formatted(userId, id));
    }

    private void removeEvict(ProceedingJoinPoint joinPoint){
        var id = (Long)joinPoint.getArgs()[0];
        var userId = (Long)joinPoint.getArgs()[1];

        friendsCache.remove(FRIENDS_KEY + id);
        friendsCache.remove(FRIENDS_KEY + userId);

        connectionCache.remove(CONNECTION_KEY.formatted(id, userId));
        connectionCache.remove(CONNECTION_KEY.formatted(userId, id));
    }

    private void snapstreakEvict(ProceedingJoinPoint joinPoint) {
        var id = (Long)joinPoint.getArgs()[0];
        var userId = (Long)joinPoint.getArgs()[1];

        snapstreakCache.remove(SNAPSTREAK_KEY.formatted(id, userId));
        snapstreakCache.remove(SNAPSTREAK_KEY.formatted(userId, id));
    }
}
