package com.robert.ernst.account.service;

import com.robert.ernst.account.dto.Account;
import com.robert.ernst.account.dto.LoginParams;
import com.robert.ernst.account.dto.RegistrationParams;
import com.robert.ernst.account.repository.AccountsRepository;
import com.robert.ernst.account.util.Validators;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AccountsRepository accountsRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public Mono<String> login(@NonNull LoginParams loginParams){
        return mapLoginParamsToAccount(loginParams).flatMap(account -> {
            if (loginParams.password().equals(account.getPassword())) {
                return Mono.just("User logged in successfully");
            }
            return Mono.error(new RuntimeException("Invalid credentials"));
        }).switchIfEmpty(Mono.error(new RuntimeException("Invalid username, email, or password")));
    }

    public Mono<Account> register(@NonNull RegistrationParams registrationParams){
        String hashedPassword = encoder.encode(registrationParams.password());

        return accountsRepository.save(new Account(
                registrationParams.username(),
                registrationParams.email(),
                hashedPassword,
                registrationParams.fullName(),
                registrationParams.phoneNumber()))
                .doOnError(err -> {
                    throw new RuntimeException("Username, email or password already used by another account");
                });
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
}
