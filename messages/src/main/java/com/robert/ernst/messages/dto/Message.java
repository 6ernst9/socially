package com.robert.ernst.messages.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name="messages")
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    private Long id;
    @NonNull
    private Long senderId;
    @NonNull
    private Long receiverId;
    @NonNull
    private String content;
    @NonNull
    private LocalDateTime timestamp;
    @NonNull
    private Boolean seen;

    public Message(@NonNull Long senderId, @NonNull Long receiverId, @NonNull String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.seen = false;
        this.timestamp = LocalDateTime.now();
    }
}
