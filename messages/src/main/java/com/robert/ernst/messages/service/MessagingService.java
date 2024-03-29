package com.robert.ernst.messages.service;

import com.robert.ernst.messages.cache.annotations.CacheableEvict;
import com.robert.ernst.messages.cache.annotations.CacheableReactive;
import com.robert.ernst.messages.dto.Message;
import com.robert.ernst.messages.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingService {
    private final WebClient webClient;
    private final MessageRepository messageRepository;
    @Value("${microservices.account.base-url}")
    private String accountsUrl;

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
        return messageRepository.getChats(senderId, receiverId);
    }

    @CacheableEvict(method = "delete")
    public Mono<String> deleteConversation(Long id, Long partner) {
        return getChats(id, partner).map(msg -> messageRepository.deleteById(msg.getId()))
                .then(Mono.just("Conversation deleted successfully"));
    }

    private Mono<Object> getUser(Long id){
        var request = webClient.get()
                .uri(accountsUrl + "/get-account/" + id)
                .accept(MediaType.APPLICATION_JSON);

        return request.exchangeToMono(response -> {
            if (response.statusCode() == HttpStatus.OK) {
                return response.bodyToMono(String.class).flatMap(Mono::just);
            } else {
                return response.bodyToMono(String.class)
                        .flatMap(err -> {
                            log.error("Request failed: {}", err);
                            return Mono.error(new RuntimeException("Request failed:" + err));
                        });
            }
        });
    }
}
