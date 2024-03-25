package com.robert.ernst.connection.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {
    private final Long id;
    private final LocalDateTime sinceConnected;
    public User(Long id){
        this.id = id;
        this.sinceConnected = LocalDateTime.now();
    }
}
