package com.robert.ernst.content.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "content")
@AllArgsConstructor
@NoArgsConstructor
public class Content {
    @Id
    private String id;
    @NonNull
    private String type;
    @NonNull
    private String url;
    @NonNull
    private Long posterId;
    @NonNull
    private LocalDateTime timestamp;
    @NonNull
    private List<Long> seen;
    @NonNull
    private List<Long> receivers;

    public Content(String type, String url, Long posterId, List<Long> receivers) {
        this.posterId = posterId;
        this.type = type;
        this.url = url;
        this.timestamp = LocalDateTime.now();
        this.seen = new ArrayList<>();
        this.receivers = receivers;
    }
}
