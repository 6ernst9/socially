package com.robert.ernst.messages.controller;

import com.robert.ernst.messages.dto.Message;
import com.robert.ernst.messages.service.MessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessagingController {
    private final MessagingService messagingService;

    @GetMapping("get-conversations/{id}")
    public Flux<Message> getConversations(@PathVariable Long id) {
        return messagingService.getConversations(id);
    }

    @GetMapping("get-chats/{sender}/{receiver}")
    public Flux<Message> getChatsWith(@PathVariable Long sender, @PathVariable Long receiver){
        return messagingService.getChats(sender, receiver);
    }

    @DeleteMapping("delete-conversation/{id}/{partner}")
    public Mono<String> deleteConversation(@PathVariable Long id, @PathVariable Long partner) {
        return messagingService.deleteConversation(id, partner);
    }
}
