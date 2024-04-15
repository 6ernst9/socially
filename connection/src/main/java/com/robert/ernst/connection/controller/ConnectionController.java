package com.robert.ernst.connection.controller;

import com.robert.ernst.connection.config.annotations.WmtSecurityMapping;
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

    @PostMapping("add-connection/{id}")
    @WmtSecurityMapping(path = "connection.add-connection")
    public Mono<Void> addConnection(@PathVariable Long id){
        return connectionService.addConnection(id).then(Mono.empty());
    }

    @PostMapping("update-snapstreak/{sender}/{receiver}")
    @WmtSecurityMapping(path = "connection.update-snapstreak")
    public Mono<String> updateStreak(@PathVariable Long sender, @PathVariable Long receiver){
        return connectionService.updateStreak(sender, receiver);
    }

    @GetMapping("get-friends/{id}")
    @WmtSecurityMapping(path = "connection.get-friends")
    public Flux<User> getAllFriends(@PathVariable Long id){
        return connectionService.getAllFriends(id);
    }

    @GetMapping("get-snapstreak/{sender}/{receiver}")
    @WmtSecurityMapping(path = "connection.get-snapstreak")
    public Mono<Integer> getSnapstreak(@PathVariable Long sender, @PathVariable Long receiver){
        return connectionService.getSnapstreak(sender, receiver);
    }

    @GetMapping("get-requests/{id}")
    @WmtSecurityMapping(path = "connection.get-requests")
    public Flux<Long> getAllRequests(@PathVariable Long id) {
        return connectionService.getAllRequests(id);
    }

    @GetMapping("get-blocked/{id}")
    @WmtSecurityMapping(path = "connection.get-blocked")
    public Flux<Long> getAllBlocked(@PathVariable Long id){
        return connectionService.getAllBlocked(id);
    }

    @GetMapping("get-connection/{firstUser}/{secondUser}")
    @WmtSecurityMapping(path = "connection.get-connection")
    public Mono<ConnectionType> getConnection(@PathVariable Long firstUser,
                                              @PathVariable Long secondUser){
        return connectionService.getConnection(firstUser, secondUser);
    }

    @PostMapping("add-friend/{firstUser}/{secondUser}")
    @WmtSecurityMapping(path = "connection.add-friend")
    public Mono<ResponseMessage<String>> addFriend(@PathVariable Long firstUser,
                                                   @PathVariable Long secondUser){
        return connectionService.addFriend(firstUser, secondUser)
                .flatMap(r -> Mono.just(new ResponseMessage<>("Friend request sent successfully")));
    }

    @PostMapping("accept-friend/{firstUser}/{secondUser}")
    @WmtSecurityMapping(path = "connection.accept-friend")
    public Mono<ResponseMessage<String>> acceptFriend(@PathVariable Long firstUser,
                                                      @PathVariable Long secondUser){
        return connectionService.acceptFriend(firstUser, secondUser)
                .flatMap(r -> Mono.just(new ResponseMessage<>("Now you are friends")));
    }

    @PostMapping("remove-friend/{firstUser}/{secondUser}")
    @WmtSecurityMapping(path = "connection.remove-friend")
    public Mono<ResponseMessage<String>> removeFriend(@PathVariable Long firstUser,
                                                      @PathVariable Long secondUser){
        return connectionService.removeFriend(firstUser, secondUser)
                .flatMap(r -> Mono.just(new ResponseMessage<>("Friend removed successfully")));
    }

    @PostMapping("block-user/{firstUser}/{secondUser}")
    @WmtSecurityMapping(path = "connection.block-user")
    public Mono<ResponseMessage<String>> blockUser(@PathVariable Long firstUser,
                                                   @PathVariable Long secondUser){
        return connectionService.blockUser(firstUser, secondUser)
                .flatMap(r -> Mono.just(new ResponseMessage<>("User blocked successfully")));
    }

    @PostMapping("unblock-user/{firstUser}/{secondUser}")
    @WmtSecurityMapping(path = "connection.unblock-user")
    public Mono<ResponseMessage<String>> unblockUser(@PathVariable Long firstUser,
                                                     @PathVariable Long secondUser){
        return connectionService.unblockUser(firstUser, secondUser)
                .flatMap(r -> Mono.just(new ResponseMessage<>("User unblocked successfully")));
    }
}
