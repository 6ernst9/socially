package com.robert.ernst.messages.service;

import com.robert.ernst.messages.cache.annotations.CacheableEvict;
import com.robert.ernst.messages.cache.annotations.CacheableReactive;
import com.robert.ernst.messages.dto.Message;
import com.robert.ernst.messages.dto.MessageRequest;
import com.robert.ernst.messages.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingService {
    private final MessageRepository messageRepository;

    @CacheableReactive(key = "conversations")
    public Flux<Message> getConversations(Long id) {
        return messageRepository.getAllConversations(id)
                .map(conv -> conv.getSenderId().equals(id)
                        ? conv.getReceiverId()
                        : conv.getSenderId())
                .collect(Collectors.toSet())
                .flux()
                .flatMap(Flux::fromIterable)
                .flatMap(partnerId -> messageRepository.getChats(id, partnerId).last());
    }

    @CacheableReactive(key = "chats")
    public Flux<Message> getChats(Long senderId, Long receiverId){
        log.info("get messages");
        return messageRepository.getChats(senderId, receiverId);
    }

    @CacheableEvict(method = "send")
    public Mono<Message> sendMessage(MessageRequest messageRequest) {
        log.info("Added message {}", messageRequest);
        return messageRepository.save(
                new Message(
                        messageRequest.senderId(),
                        messageRequest.receiverId(),
                        messageRequest.content()));
    }

    @CacheableEvict(method = "read")
    public Mono<Message> readMessage(Long id) {
        return messageRepository.findById(id)
                .flatMap(message -> {
                    message.setSeen(true);

                    return messageRepository.save(message);
                });
    }

    @CacheableEvict(method = "delete")
    public Mono<String> deleteConversation(Long id, Long partner) {
        return messageRepository.getChats(id, partner).flatMap(msg -> {
            var visibility = msg.getVisibility();
            visibility.remove(id);

            msg.setVisibility(visibility);
            return messageRepository.save(msg);
        }).then(Mono.just("Conversation deleted successfully"));
    }
}
