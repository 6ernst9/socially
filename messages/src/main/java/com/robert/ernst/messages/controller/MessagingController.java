package com.robert.ernst.messages.controller;

import com.robert.ernst.messages.config.annotations.WmtSecurityMapping;
import com.robert.ernst.messages.dto.Message;
import com.robert.ernst.messages.dto.MessageRequest;
import com.robert.ernst.messages.service.MessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessagingController {
    private final MessagingService messagingService;

    @SendTo("/topic/reply")
    @MessageMapping("/sendMessage")
    public Mono<Message> sendMessage(@Payload MessageRequest message) {
        return messagingService.sendMessage(message);
    }

    @MessageMapping("/readMessage")
    public Mono<Message> readMessage(@Payload Long id) {
        return messagingService.readMessage(id);
    }

    @GetMapping("get-conversations/{id}")
    @WmtSecurityMapping(path = "messaging.get-conversations")
    public Flux<Message> getConversations(@PathVariable Long id) {
        return messagingService.getConversations(id);
    }

    @GetMapping("get-chats/{sender}/{receiver}")
    @WmtSecurityMapping(path = "messaging.get-chats")
    public Flux<Message> getChatsWith(@PathVariable Long sender, @PathVariable Long receiver){
        return messagingService.getChats(sender, receiver);
    }

    @DeleteMapping("delete-conversation/{id}/{partner}")
    @WmtSecurityMapping(path = "messaging.delete-conversation")
    public Mono<String> deleteConversation(@PathVariable Long id, @PathVariable Long partner) {
        return messagingService.deleteConversation(id, partner);
    }
}
