package com.robert.ernst.messages.cache;

import com.robert.ernst.messages.cache.annotations.CacheableEvict;
import com.robert.ernst.messages.cache.annotations.CacheableReactive;
import com.robert.ernst.messages.cache.dto.CacheEntry;
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
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final String CONVERSATION_KEY = "conversation:";
    private final String CHAT_KEY = "chat:%s:%s";

    @Around("@annotation(caching)")
    public Object cache(ProceedingJoinPoint joinPoint, CacheableReactive caching) throws Throwable {
        if(caching.value().equals("conversation")){
            return cacheConversation(joinPoint);
        } else {
            return cacheChat(joinPoint);
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

    private Object cacheConversation(ProceedingJoinPoint joinPoint) throws Throwable {
        var id = (Long)joinPoint.getArgs()[0];
        var key = CONVERSATION_KEY + id;

        var entry = cache.get(key);

        if(entry!=null && !entry.isExpired()){
            return Flux.fromIterable((Iterable<?>)entry.getValue());
        } else {
            var result = (Flux<?>)joinPoint.proceed();
            var conversation = result.toIterable();

            cache.put(key, new CacheEntry(conversation));
            return result;
        }
    }

    private Object cacheChat(ProceedingJoinPoint joinPoint) throws Throwable {
        var senderId = (Long)joinPoint.getArgs()[0];
        var receiverId = (Long)joinPoint.getArgs()[1];
        var key = CHAT_KEY.formatted(senderId, receiverId);

        var entry = cache.get(key);

        if(entry!=null && !entry.isExpired()){
            return Flux.fromIterable((Iterable<?>)entry.getValue());
        } else {
            var result = (Flux<?>)joinPoint.proceed();
            var chat = result.toIterable();

            cache.put(key, new CacheEntry(chat));
            return result;
        }
    }

    private void updateEvict(ProceedingJoinPoint joinPoint){

    }

    private void deleteEvict(ProceedingJoinPoint joinPoint){
        Long senderId = (Long)joinPoint.getArgs()[0];
        Long receiverId = (Long)joinPoint.getArgs()[1];

        cache.remove(CHAT_KEY.formatted(senderId, receiverId));
        cache.remove(CONVERSATION_KEY + senderId);
    }


}
