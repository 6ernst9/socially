package com.robert.ernst.messages.service;

import com.robert.ernst.messages.cache.Caching;
import com.robert.ernst.messages.dto.Message;
import com.robert.ernst.messages.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingService {
    private final WebClient webClient;
    private final MessageRepository messageRepository;
    @Value("${microservices.account.base-url}")
    private String accountsUrl;

    @Caching(value = "conversation")
    public List<Message> getConversations(Long id) {
        var conversations = new ArrayList<Message>();
        var conversationPartners = new HashSet<Long>();

        messageRepository.getAllConversations(id)
                .forEach(conv -> {
                    if(conv.getSenderId().equals(id)) {
                        conversationPartners.add(conv.getReceiverId());
                    } else {
                        conversationPartners.add(conv.getSenderId());
                    }
                });

        conversationPartners.forEach(partnerId -> {
            var messages = messageRepository.getChats(id, partnerId);
            var latestSent = messages.get(messages.size()-1);

            conversations.add(latestSent);

        });
        return conversations;
    }

    @Caching(value = "chats")
    public List<Message> getChats(Long senderId, Long receiverId){
        return messageRepository.getChats(senderId, receiverId);
    }

    @Transactional
    public String deleteConversation(Long id, Long partner) {
        getChats(id, partner).forEach(msg -> messageRepository.deleteById(msg.getId()));

        return "Conversation deleted successfully";
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
