package com.robert.ernst.messages.repository;


import com.robert.ernst.messages.dto.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m WHERE m.senderId = :id OR m.receiverId = :id ORDER BY m.timestamp")
    List<Message> getAllConversations(Long id);

    @Query("SELECT m FROM Message m " +
            "WHERE m.senderId = :sender AND m.receiverId = :receiver " +
            "OR m.senderId = :receiver AND m.receiverId = :sender " +
            "ORDER BY m.timestamp")
    List<Message> getChats(Long sender, Long receiver);
}
