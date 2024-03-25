package com.robert.ernst.account.cache;

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
    private final Map<String, CacheEntry> usernameCache = new ConcurrentHashMap<>();
    private final Map<Long, CacheEntry> idCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry> termCache = new ConcurrentHashMap<>();

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
        String term = (String)joinPoint.getArgs()[0];
        var entry = termCache.get(term);

        if(entry!=null && !entry.isExpired()){
            return Flux.fromIterable((Iterable<?>)entry.getValue());
        } else {
            var result = (Flux<?>)joinPoint.proceed();
            var results = result.toIterable();

            termCache.put(term, new CacheEntry(results));
            return result;
        }
    }

    private Object cacheId(ProceedingJoinPoint joinPoint) throws Throwable {
        Long id = (Long)joinPoint.getArgs()[0];
        var entry = idCache.get(id);

        if(entry!=null && !entry.isExpired()){
            return Mono.just(entry.getValue());
        } else {
            var result = (Mono<?>)joinPoint.proceed();
            var account = result.block();

            idCache.put(id, new CacheEntry(account));
            return result;
        }
    }

    private Object cacheUsername(ProceedingJoinPoint joinPoint) throws Throwable {
        String username = (String)joinPoint.getArgs()[0];
        var entry = usernameCache.get(username);

        if(entry!=null && !entry.isExpired()){
            return Mono.just(entry.getValue());
        } else {
            var result = (Mono<?>)joinPoint.proceed();
            var account = result.block();

            usernameCache.put(username, new CacheEntry(account));
            return result;
        }
    }

    private void updateEvict(ProceedingJoinPoint joinPoint){
        Account account = (Account)joinPoint.getArgs()[0];
        idCache.remove(account.getId());
        usernameCache.remove(account.getUsername());
        termCache.clear();
    }

    private void deleteEvict(ProceedingJoinPoint joinPoint){
        Long id = (Long)joinPoint.getArgs()[1];
        idCache.remove(id);
        usernameCache.clear();
        termCache.clear();
    }


}
