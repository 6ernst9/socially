package com.robert.ernst.connection.controller;

import com.robert.ernst.connection.dto.ConnectionRequest;
import com.robert.ernst.connection.dto.ConnectionType;
import com.robert.ernst.connection.dto.ResponseMessage;
import com.robert.ernst.connection.dto.User;
import com.robert.ernst.connection.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ConnectionController {
    private final ConnectionService connectionService;
    @GetMapping("get-friends/{id}")
    public Flux<User> getAllFriends(@PathVariable Long id){
        return connectionService.getAllFriends(id);
    }

    @GetMapping("get-requests/{id}")
    public Flux<Long> getAllRequests(@PathVariable Long id) {
        return connectionService.getAllRequests(id);
    }

    @GetMapping("get-blocked/{id}")
    public Flux<Long> getAllBlocked(@PathVariable Long id){
        return connectionService.getAllBlocked(id);
    }

    @GetMapping("get-connection/{firstUser}/{secondUser}")
    public Mono<ConnectionType> getConnection(@PathVariable Long firstUser,
                                              @PathVariable Long secondUser){
        return connectionService.getConnection(firstUser, secondUser);
    }

    @PostMapping("add-friend")
    public Mono<ResponseMessage<String>> addFriend(@RequestBody ConnectionRequest request){
        return connectionService.addFriend(request.id(), request.userId())
                .flatMap(r -> Mono.just(new ResponseMessage<>("Friend request sent successfully")));
    }

    @PostMapping("accept-friend")
    public Mono<ResponseMessage<String>> acceptFriend(@RequestBody ConnectionRequest request){
        return connectionService.acceptFriend(request.id(), request.userId())
                .flatMap(r -> Mono.just(new ResponseMessage<>("Now you are friends")));
    }

    @PostMapping("remove-friend")
    public Mono<ResponseMessage<String>> removeFriend(@RequestBody ConnectionRequest request){
        return connectionService.removeFriend(request.id(), request.userId())
                .flatMap(r -> Mono.just(new ResponseMessage<>("Friend removed successfully")));
    }

    @PostMapping("block-user")
    public Mono<ResponseMessage<String>> blockUser(@RequestBody ConnectionRequest request){
        return connectionService.blockUser(request.id(), request.userId())
                .flatMap(r -> Mono.just(new ResponseMessage<>("User blocked successfully")));
    }

    @PostMapping("unblock-user")
    public Mono<ResponseMessage<String>> unblockUser(@RequestBody ConnectionRequest request){
        return connectionService.unblockUser(request.id(), request.userId())
                .flatMap(r -> Mono.just(new ResponseMessage<>("User unblocked successfully")));
    }
}
