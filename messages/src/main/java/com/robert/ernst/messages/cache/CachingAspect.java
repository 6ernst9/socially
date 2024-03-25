package com.robert.ernst.messages.cache;

import com.robert.ernst.messages.dto.Message;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class CachingAspect {
    private final Map<Long, CacheEntry> conversationCache = new ConcurrentHashMap<>();
    private final Map<CacheKey, CacheEntry> chatCache = new ConcurrentHashMap<>();

    @Around("@annotation(caching)")
    public Object cache(ProceedingJoinPoint joinPoint, Caching caching) throws Throwable {
        if(caching.value().equals("conversation")){
            return cacheConversation(joinPoint);
        } else {
            return cacheChat(joinPoint);
        }
    }

    @Around("@annotation(evict)")
    public Object evict(ProceedingJoinPoint joinPoint, CachingEvict evict) throws Throwable {
        if(evict.method().equals("update")){
           updateEvict(joinPoint);
        } else {
            deleteEvict(joinPoint);
        }
        return joinPoint.proceed();
    }

    private Object cacheConversation(ProceedingJoinPoint joinPoint) throws Throwable {
        Long id = (Long)joinPoint.getArgs()[0];
        var entry = conversationCache.get(id);

        if(entry!=null && !entry.isExpired()){
            return entry.getValue();
        } else {
            var conversation = joinPoint.proceed();
            conversationCache.put(id, new CacheEntry(conversation));
            return conversation;
        }
    }

    private Object cacheChat(ProceedingJoinPoint joinPoint) throws Throwable {
        var senderId = (Long)joinPoint.getArgs()[0];
        var receiverId = (Long)joinPoint.getArgs()[1];

        var key = new CacheKey(senderId, receiverId);
        var entry = chatCache.get(key);

        if(entry!=null && !entry.isExpired()){
            return entry.getValue();
        } else {
            var chat = joinPoint.proceed();
            chatCache.put(key, new CacheEntry(chat));
            return chat;
        }
    }

    private void updateEvict(ProceedingJoinPoint joinPoint){

    }

    private void deleteEvict(ProceedingJoinPoint joinPoint){
        Long senderId = (Long)joinPoint.getArgs()[0];
        Long receiverId = (Long)joinPoint.getArgs()[1];
        chatCache.remove(new CacheKey(senderId, receiverId));

        List<Message> conversations = (List<Message>)conversationCache.get(senderId).getValue();
        var newConversation = conversations.stream()
                .filter(msg -> msg.getSenderId().equals(receiverId) && msg.getReceiverId().equals(senderId)
                        || msg.getReceiverId().equals(receiverId) && msg.getSenderId().equals(senderId));

        conversationCache.put(senderId, new CacheEntry(newConversation));
    }


}
