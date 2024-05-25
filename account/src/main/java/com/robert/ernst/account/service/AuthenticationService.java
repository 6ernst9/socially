package com.robert.ernst.account.service;

import com.robert.ernst.account.dto.*;
import com.robert.ernst.account.repository.AccountsRepository;
import com.robert.ernst.account.repository.SessionRepository;
import com.robert.ernst.account.util.Validators;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AccountsRepository accountsRepository;
    private final SessionRepository sessionRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Mono<Long> login(@NonNull LoginParams loginParams){
        var headers = RequestContext.getHeaders();

        return mapLoginParamsToAccount(loginParams).flatMap(account -> {
            if (loginParams.password().equals(account.getPassword())) {
                return setSession(account.getId(), headers).map(Session::getId);
            }
            return Mono.error(new RuntimeException("Incorrect password"));
        }).switchIfEmpty(Mono.error(new RuntimeException("Invalid username, email or phone number")));
    }

    public Mono<Long> register(@NonNull RegistrationParams registrationParams){
        String hashedPassword = encoder.encode(registrationParams.password());
        var headers = RequestContext.getHeaders();

        var account = accountsRepository.save(new Account(
                registrationParams.username(),
                registrationParams.email(),
                hashedPassword,
                registrationParams.fullName(),
                registrationParams.phoneNumber()))
                .doOnError(err -> {
                    throw new RuntimeException("Username, email or phone number already used by another account");
                });

        return account.flatMap(acc -> setSession(acc.getId(), headers).map(Session::getId));
    }

    public Mono<String> logout(Long id) {
        return sessionRepository.deleteById(id).then(Mono.just("User logged out successfully"));
    }

    private Mono<Account> mapLoginParamsToAccount(LoginParams loginParams){
        if(loginParams.username().contains("@")){
            return accountsRepository.findAccountByEmail(loginParams.username());
        } else if(Validators.isValidPhoneNumber(loginParams.username())){
            return accountsRepository.findAccountByPhoneNumber(loginParams.username());
        } else {
            return accountsRepository.findAccountByUsername(loginParams.username());
        }
    }

    private Mono<Session> setSession(Long id, Map<String, String> headers) {
        return sessionRepository.save(
                new Session(
                    id,
                    headers.get("X-FI-SY-IP"),
                    headers.get("X-FI-SY-DEVICE"),
                    headers.get("X-FI-SY-SITE-ID"))
        );
    }
}
