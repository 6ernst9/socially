package com.robert.ernst.account.controller;

import com.robert.ernst.account.config.annotations.WmtSecurityMapping;
import com.robert.ernst.account.dto.RegistrationParams;
import com.robert.ernst.account.dto.LoginParams;
import com.robert.ernst.account.dto.ResponseMessage;
import com.robert.ernst.account.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @WmtSecurityMapping(path="account.login")
    public Mono<Long> login(@RequestBody LoginParams params){
        return authenticationService.login(params);
    }

    @PostMapping("/register")
    @WmtSecurityMapping(path="account.register")
    public Mono<Long> register(@Validated @RequestBody RegistrationParams params){
        return authenticationService.register(params);
    }

    @PostMapping("/logout/{id}")
    @WmtSecurityMapping(path="account.logout")
    public Mono<ResponseMessage<String>> logout(@PathVariable Long id){
        return authenticationService.logout(id)
                .flatMap(res -> Mono.just(new ResponseMessage<>(res)));
    }
}
