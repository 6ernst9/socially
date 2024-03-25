package com.robert.ernst.account.controller;

import com.robert.ernst.account.dto.RegistrationParams;
import com.robert.ernst.account.dto.LoginParams;
import com.robert.ernst.account.dto.ResponseMessage;
import com.robert.ernst.account.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public Mono<ResponseMessage<String>> login(@RequestBody LoginParams params){
        return authenticationService.login(params)
                .flatMap(res -> Mono.just(new ResponseMessage<>(res)));
    }

    @PostMapping("/register")
    public Mono<ResponseMessage<String>> register(@Validated @RequestBody RegistrationParams params){
        return authenticationService.register(params)
                .flatMap(res -> Mono.just(new ResponseMessage<>("User registered successfully")));
    }
}
