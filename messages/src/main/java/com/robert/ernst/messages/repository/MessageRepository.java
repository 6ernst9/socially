package com.robert.ernst.messages.repository;

import com.robert.ernst.messages.dto.Message;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface MessageRepository extends ReactiveCrudRepository<Message, Long> {
    @Query("SELECT * FROM messages WHERE sender_id = :id OR receiver_id = :id ORDER BY timestamp")
    Flux<Message> getAllConversations(Long id);

    @Query("SELECT * FROM messages " +
            "WHERE sender_id = :sender AND receiver_id = :receiver " +
            "OR sender_id = :receiver AND receiver_id = :sender " +
            "ORDER BY timestamp")
    Flux<Message> getChats(Long sender, Long receiver);
}
