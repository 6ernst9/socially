package com.robert.ernst.messages.cache;

import com.robert.ernst.messages.cache.annotations.CacheableEvict;
import com.robert.ernst.messages.cache.annotations.CacheableReactive;
import com.robert.ernst.messages.cache.dto.CacheEntry;
import com.robert.ernst.messages.dto.Message;
import com.robert.ernst.messages.dto.MessageRequest;
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
    private final Map<String, CacheEntry> chatsCache = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry> conversationCache= new ConcurrentHashMap<>();
    private final String CONVERSATION_KEY = "messaging:conversation:";
    private final String CHAT_KEY = "messaging:chat:%s:%s";

    @Around("@annotation(caching)")
    public Object cache(ProceedingJoinPoint joinPoint, CacheableReactive caching) throws Throwable {
        if(caching.key().equals("conversations")){
            return cacheConversation(joinPoint);
        } else {
            return cacheChat(joinPoint);
        }
    }

    @Around("@annotation(evict)")
    public Object evict(ProceedingJoinPoint joinPoint, CacheableEvict evict) throws Throwable {
        if(evict.method().equals("read")) {
            return readEvict(joinPoint);
        } else if(evict.method().equals("delete")) {
            deleteEvict(joinPoint);
        } else {
            sendEvict(joinPoint);
        }

        return joinPoint.proceed();
    }

    private Object cacheConversation(ProceedingJoinPoint joinPoint) throws Throwable {
        var id = (Long)joinPoint.getArgs()[0];
        var key = CONVERSATION_KEY + id;

        var entry = conversationCache.get(key);

        if(entry!=null && !entry.isExpired()){
            return Flux.fromIterable((Iterable<?>)entry.getValue());
        } else {
            var result = (Flux<?>)joinPoint.proceed();
            var conversation = result.toIterable();

            conversationCache.put(key, new CacheEntry(conversation));
            return result;
        }
    }

    private Object cacheChat(ProceedingJoinPoint joinPoint) throws Throwable {
        var senderId = (Long)joinPoint.getArgs()[0];
        var receiverId = (Long)joinPoint.getArgs()[1];
        var key = CHAT_KEY.formatted(senderId, receiverId);

        var entry = chatsCache.get(key);

        if(entry!=null && !entry.isExpired()){
            return Flux.fromIterable((Iterable<?>)entry.getValue());
        } else {
            var result = (Flux<?>)joinPoint.proceed();
            var chat = result.toIterable();

            chatsCache.put(key, new CacheEntry(chat));
            return result;
        }
    }

    private Object readEvict(ProceedingJoinPoint joinPoint) throws Throwable {
        var result = (Mono<Message>)joinPoint.proceed();
        var message = result.block();

        var senderId = message.getSenderId();
        var receiverId = message.getReceiverId();

        removeCache(senderId, receiverId);
        return result;
    }

    private void deleteEvict(ProceedingJoinPoint joinPoint) {
        var senderId = (Long)joinPoint.getArgs()[0];
        var receiverId = (Long)joinPoint.getArgs()[1];

        removeCache(senderId, receiverId);
    }

    private void sendEvict(ProceedingJoinPoint joinPoint) {
        var message = (MessageRequest)joinPoint.getArgs()[0];
        var senderId = message.senderId();
        var receiverId = message.receiverId();

        removeCache(senderId, receiverId);
    }


    private void removeCache(Long senderId, Long receiverId) {
        chatsCache.remove(CHAT_KEY.formatted(senderId, receiverId));
        conversationCache.remove(CONVERSATION_KEY + senderId);
        conversationCache.remove(CONVERSATION_KEY + receiverId);
    }
}
