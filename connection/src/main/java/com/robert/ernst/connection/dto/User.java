package com.robert.ernst.connection.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private LocalDateTime sinceConnected;
    public User(Long id){
        this.id = id;
        this.sinceConnected = LocalDateTime.now();
    }
}
