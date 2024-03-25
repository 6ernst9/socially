package com.robert.ernst.account.service;

import com.robert.ernst.account.cache.CacheableReactive;
import com.robert.ernst.account.cache.CacheableEvict;
import com.robert.ernst.account.dto.Account;
import com.robert.ernst.account.repository.AccountsRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountsRepository accountsRepository;

    @CacheableReactive(key = "id")
    public Mono<Account> getAccountById(@NonNull Long id){
        return accountsRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")));
    }

    @CacheableReactive(key = "username")
    public Mono<Account> getAccountByUsername(@NonNull String username){
        return accountsRepository.findAccountByUsername(username)
                .switchIfEmpty(Mono.error( new RuntimeException("User not found")));
    }

    @CacheableEvict(method = "update")
    public Mono<Account> updateAccount(Account accountToUpdate) {
        return accountsRepository.findById(accountToUpdate.getId())
                .flatMap(account -> accountsRepository.save(accountToUpdate))
                .switchIfEmpty(Mono.error(new RuntimeException("Account not found")));
    }

    @CacheableReactive(key="term")
    public Flux<Account> getAccountsByTerm(@NonNull String term){
        return accountsRepository.findAll()
                .filter(account -> account.getEmail().contains(term) ||
                        account.getUsername().contains(term) ||
                        account.getFullName().equals(term));
    }

    @CacheableEvict(method="delete")
    public Mono<Void> deleteAccount(@NonNull Long id, @NonNull Long deleteId){
        return accountsRepository.findById(id)
                .flatMap(admin -> {
                    if(!admin.getIsAdmin()){
                        return Mono.error(new RuntimeException("Invalid rights."));
                    } else {
                        return accountsRepository.findById(deleteId)
                                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                                .flatMap(account -> accountsRepository.deleteById(deleteId));
                    }
                });
    }
}
