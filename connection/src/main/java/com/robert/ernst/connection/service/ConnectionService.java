package com.robert.ernst.connection.service;

import com.robert.ernst.connection.cache.annotations.CacheableEvict;
import com.robert.ernst.connection.cache.annotations.CacheableReactive;
import com.robert.ernst.connection.dto.Connection;
import com.robert.ernst.connection.dto.ConnectionType;
import com.robert.ernst.connection.dto.User;
import com.robert.ernst.connection.repository.ConnectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ConnectionService {
    private final ConnectionRepository connectionRepository;

    @CacheableEvict(method = "requested")
    public Mono<Connection> addFriend(Long id, Long userId){
        return connectionRepository.findById(userId)
                .flatMap(connection -> {
                    connection.getRequests().add(id);
                    return connectionRepository.save(connection);
                });
    }

    @CacheableEvict(method = "accepted")
    public Mono<Connection> acceptFriend(Long id, Long userId){
        return connectionRepository.findById(id)
                .flatMap(connection -> {
                    connection.getRequests().remove(userId);
                    return connectionRepository.save(connection);
                })
                .then(connectionRepository.findById(id)
                        .flatMap(connection -> {
                            connection.getFriends().add(new User(userId));
                            return connectionRepository.save(connection);
                        })
                        .then(connectionRepository.findById(userId)
                                .flatMap(connection -> {
                                    connection.getFriends().add(new User(id));
                                    return connectionRepository.save(connection);
                                })));
    }

    @CacheableEvict(method = "removed")
    public Mono<Connection> removeFriend(Long id, Long userId){
        return connectionRepository.findById(id)
                .flatMap(connection -> {
                    var friends = connection.getFriends()
                            .stream()
                            .filter(friend -> !friend.getId().equals(userId))
                            .toList();
                    connection.setFriends(friends);
                    return connectionRepository.save(connection);
                })
                .then(connectionRepository.findById(userId)
                        .flatMap(connection -> {
                            var friends = connection.getFriends()
                                    .stream()
                                    .filter(friend -> !friend.getId().equals(id))
                                    .toList();
                            connection.setFriends(friends);
                            return connectionRepository.save(connection);
                        }));
    }

    @CacheableEvict(method = "blocked")
    public Mono<Connection> blockUser(Long id, Long userId){
        return removeFriend(id, userId)
                .then(connectionRepository.findById(id)
                        .flatMap(connection -> {
                            connection.getBlocked().add(userId);
                            return connectionRepository.save(connection);
                        }));
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

    @CacheableReactive(key = "connection")
    public Mono<ConnectionType> getConnection(Long id, Long userId) {
        Mono<Connection> firstUserConnection = connectionRepository.findById(id);
        Mono<Connection> secondUserConnection = connectionRepository.findById(userId);

        return Mono.zip(firstUserConnection, secondUserConnection)
                .map(tuple -> {
                    var userConn = tuple.getT1();
                    var targetConn = tuple.getT2();

                    var areFriends = userConn.getFriends().stream().anyMatch(friend -> friend.getId().equals(userId));
                    var userRequestedTarget = targetConn.getRequests().stream().anyMatch(request -> request.equals(id));
                    var targetRequestedUser = userConn.getRequests().stream().anyMatch(request -> request.equals(userId));
                    var userBlockedTarget = userConn.getBlocked().contains(userId);
                    var targetBlockedUser = targetConn.getBlocked().contains(id);

                    if (areFriends) {
                        return ConnectionType.FRIENDS;
                    } else if(userRequestedTarget){
                        return ConnectionType.REQUESTING;
                    } else if(targetRequestedUser) {
                        return ConnectionType.REQUESTED;
                    } else if (userBlockedTarget) {
                        return ConnectionType.BLOCKING;
                    } else if (targetBlockedUser) {
                        return ConnectionType.BLOCKED;
                    } else {
                        return ConnectionType.NONE;
                    }
                });
    }

    @CacheableReactive(key = "friends")
    public Flux<User> getAllFriends(Long id) {
        return connectionRepository.findById(id)
                .flatMapMany(connection -> Flux.fromIterable(connection.getFriends()));
    }

    @CacheableReactive(key = "requests")
    public Flux<Long> getAllRequests(Long id) {
        return connectionRepository.findById(id)
                .flatMapMany(connection -> Flux.fromIterable(connection.getRequests()));
    }

    @CacheableReactive(key = "blocked")
    public Flux<Long> getAllBlocked(Long id) {
        return connectionRepository.findById(id)
                .flatMapMany(connection -> Flux.fromIterable(connection.getBlocked()));
    }
}
