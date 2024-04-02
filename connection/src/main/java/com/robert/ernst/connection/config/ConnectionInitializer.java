package com.robert.ernst.connection.config;

import com.robert.ernst.connection.dto.Connection;
import com.robert.ernst.connection.repository.ConnectionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ConnectionInitializer {
    private final ConnectionRepository repository;

    @PostConstruct
    void initConnections(){
        var connection1 = new Connection(1L);
        var connection2 = new Connection(2L);
        var connection3 = new Connection(3L);
        var connection4 = new Connection(4L);
        var connection5 = new Connection(5L);
        var connections = List.of(connection1, connection2, connection3, connection4, connection5);

        repository.deleteAll().subscribe();
        repository.saveAll(connections).subscribe();
    }

}
