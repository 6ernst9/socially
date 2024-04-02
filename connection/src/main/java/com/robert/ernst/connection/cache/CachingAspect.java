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
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final String FRIENDS_KEY = "friends:";
    private final String BLOCKED_KEY = "blocked:";
    private final String REQUESTS_KEY = "requests:";
    private final String CONNECTION_KEY = "connection:%s:%s";

    @Around("@annotation(caching)")
    public Object cache(ProceedingJoinPoint joinPoint, CacheableReactive caching) throws Throwable {
        return switch (caching.key()) {
            case "requests" -> cache(joinPoint, REQUESTS_KEY);
            case "friends" -> cache(joinPoint, FRIENDS_KEY);
            case "blocked" -> cache(joinPoint, BLOCKED_KEY);
            case "connection" -> cacheConnection(joinPoint);
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
        }
        return joinPoint.proceed();
    }

    private Object cache(ProceedingJoinPoint joinPoint, String key) throws Throwable {
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

    private Object cacheConnection(ProceedingJoinPoint joinPoint) throws Throwable {
        var id = (Long)joinPoint.getArgs()[0];
        var userId = (Long)joinPoint.getArgs()[1];

        var key = CONNECTION_KEY.formatted(id, userId);
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

        cache.forEach((key, value) -> {
            if(key.equals(FRIENDS_KEY + id)
                    || key.equals(FRIENDS_KEY + userId)
                    || key.equals(REQUESTS_KEY + userId)
                    || key.equals(CONNECTION_KEY.formatted(id, userId))
                    || key.equals(CONNECTION_KEY.formatted(userId, id))){
                cache.remove(key);
            }
        });
    }

    private void requestEvict(ProceedingJoinPoint joinPoint) {
        var id = (Long)joinPoint.getArgs()[0];
        var userId = (Long)joinPoint.getArgs()[1];

        cache.forEach((key, value) -> {
            if(key.equals(REQUESTS_KEY + userId)
                    || key.equals(CONNECTION_KEY.formatted(id, userId))
                    || key.equals(CONNECTION_KEY.formatted(userId, id))){
                cache.remove(key);
            }
        });
    }

    private void blockEvict(ProceedingJoinPoint joinPoint) {
        var id = (Long)joinPoint.getArgs()[0];
        var userId = (Long)joinPoint.getArgs()[1];

        cache.forEach((key, value) -> {
            if(key.equals(FRIENDS_KEY + id)
                    || key.equals(FRIENDS_KEY + userId)
                    || key.equals(BLOCKED_KEY + id)
                    || key.equals(CONNECTION_KEY.formatted(id, userId))
                    || key.equals(CONNECTION_KEY.formatted(userId, id))){
                cache.remove(key);
            }
        });
    }

    private void removeEvict(ProceedingJoinPoint joinPoint){
        var id = (Long)joinPoint.getArgs()[0];
        var userId = (Long)joinPoint.getArgs()[1];

        cache.forEach((key, value) -> {
            if(key.equals(FRIENDS_KEY + id)
                    || key.equals(FRIENDS_KEY + userId)
                    || key.equals(CONNECTION_KEY.formatted(id, userId))
                    || key.equals(CONNECTION_KEY.formatted(userId, id))){
                cache.remove(key);
            }
        });
    }
}
