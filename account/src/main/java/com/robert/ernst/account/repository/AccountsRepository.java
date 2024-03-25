package com.robert.ernst.account.repository;

import com.robert.ernst.account.dto.Account;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountsRepository extends ReactiveCrudRepository<Account, Long> {
    @Query("SELECT * FROM accounts WHERE username = :username")
    Mono<Account> findAccountByUsername(String username);

    @Query("SELECT * FROM accounts WHERE email = :email")
    Mono<Account> findAccountByEmail(String email);

    @Query("SELECT * FROM accounts WHERE phone_number = :phoneNumber")
    Mono<Account> findAccountByPhoneNumber(String phoneNumber);

    @Query("SELECT COUNT(*) > 0 FROM accounts WHERE username = :username")
    Mono<Boolean> existsByUsername(String username);

    @Query("SELECT COUNT(*) > 0 FROM accounts WHERE email = :email")
    Mono<Boolean> existsByEmail(String email);

    @Query("SELECT COUNT(*) > 0 FROM accounts WHERE phone_number = :phoneNumber")
    Mono<Boolean> existsByPhoneNumber(String phoneNumber);
}
