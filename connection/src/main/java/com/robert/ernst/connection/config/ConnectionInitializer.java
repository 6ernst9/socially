package com.robert.ernst.connection.config;

import com.robert.ernst.connection.dto.Connection;
import com.robert.ernst.connection.dto.Streak;
import com.robert.ernst.connection.dto.User;
import com.robert.ernst.connection.repository.ConnectionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;
@Configuration
@RequiredArgsConstructor
public class ConnectionInitializer {
    private final ConnectionRepository repository;

    @PostConstruct
    void initContents(){
        var connection1 = new Connection(1L, List.of(new User(2L), new User(3L)), List.of(new Streak(244, 2L, LocalDate.now()), new Streak(21, 3L, LocalDate.now())));
        var connection2 = new Connection(2L,List.of(new User(1L), new User(3L), new User(4L)));
        var connection3 = new Connection(3L, List.of(new User(2L), new User(1L)));
        var connection4 = new Connection(4L,  List.of(new User(5L), new User(2L)));
        var connection5 = new Connection(5L,  List.of(new User(4L)));
        var connections = List.of(connection1, connection2, connection3, connection4, connection5);

        repository.deleteAll().subscribe();
        repository.saveAll(connections).subscribe();
    }

}
