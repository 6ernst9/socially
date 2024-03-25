package com.robert.ernst.connection.service;

import com.robert.ernst.connection.dto.Connection;
import com.robert.ernst.connection.dto.User;
import com.robert.ernst.connection.repository.ConnectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConnectionService {
    private final ConnectionRepository connectionRepository;

    public Mono<Connection> addFriend(Long id, Long userId){
        return connectionRepository.findById(id)
                .flatMap(connection -> {
                    connection.getFriends().add(new User(userId, LocalDateTime.now()));
                    return connectionRepository.save(connection);
                });
    }

    public Mono<Connection> removeFriend(Long id, Long userId){
        return connectionRepository.findById(id)
                .flatMap(connection -> {
                    var friends = connection.getFriends()
                            .stream()
                            .filter(friend -> !friend.id().equals(userId))
                            .toList();
                    connection.setFriends(friends);
                    return connectionRepository.save(connection);
                });
    }

    public Mono<Connection> blockUser(Long id, Long userId){
        return connectionRepository.findById(id)
                .flatMap(connection -> {
                    connection.getBlocked().add(userId);
                    return connectionRepository.save(connection);
                });
    }

    public Mono<Connection> unblockUser(Long id, Long userId){
        return connectionRepository.findById(id)
                .flatMap(connection -> {
                    var blocked = connection.getBlocked()
                            .stream()
                            .filter(blockedId -> !blockedId.equals(userId))
                            .toList();
                    connection.setBlocked(blocked);
                    return connectionRepository.save(connection);
                });
    }
}
