package com.robert.ernst.account.cache;

import com.robert.ernst.account.cache.annotations.CacheableEvict;
import com.robert.ernst.account.cache.annotations.CacheableReactive;
import com.robert.ernst.account.cache.dto.CacheEntry;
import com.robert.ernst.account.dto.Account;
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
    private final String ID_KEY = "id:";
    private final String USERNAME_KEY = "username:";
    private final String TERM_KEY = "term:";

    @Around("@annotation(caching)")
    public Object cache(ProceedingJoinPoint joinPoint, CacheableReactive caching) throws Throwable {
        if(caching.key().equals("username")){
            return cacheUsername(joinPoint);
        } else if(caching.key().equals("id")){
            return cacheId(joinPoint);
        } else {
            return cacheTerm(joinPoint);
        }
    }

    @Around("@annotation(evict)")
    public Object evict(ProceedingJoinPoint joinPoint, CacheableEvict evict) throws Throwable {
        if(evict.method().equals("update")){
           updateEvict(joinPoint);
        } else {
            deleteEvict(joinPoint);
        }
        return joinPoint.proceed();
    }

    private Object cacheTerm(ProceedingJoinPoint joinPoint) throws Throwable {
        var term = (String)joinPoint.getArgs()[0];
        var key = TERM_KEY + term;

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

    private Object cacheId(ProceedingJoinPoint joinPoint) throws Throwable {
        var id = (Long)joinPoint.getArgs()[0];
        var key = ID_KEY + id;

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

    private Object cacheUsername(ProceedingJoinPoint joinPoint) throws Throwable {
        String username = (String)joinPoint.getArgs()[0];
        var key = USERNAME_KEY + username;

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

    private void updateEvict(ProceedingJoinPoint joinPoint){
        var account = (Account)joinPoint.getArgs()[0];
        cache.remove(ID_KEY + account.getId());
        cache.remove(USERNAME_KEY + account.getUsername());

        cache.forEach((key, value) -> {
            if (key.contains(TERM_KEY)) {
                cache.remove(key);
            }
        });
    }

    private void deleteEvict(ProceedingJoinPoint joinPoint){
        var id = (Long)joinPoint.getArgs()[1];
        cache.remove(ID_KEY + id);

        cache.forEach((key, value) -> {
            if (key.contains(USERNAME_KEY) || key.contains(TERM_KEY)) {
                cache.remove(key);
            }
        });
    }
}
