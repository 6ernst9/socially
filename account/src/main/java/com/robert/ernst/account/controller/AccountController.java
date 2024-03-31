package com.robert.ernst.account.controller;

import com.robert.ernst.account.config.annotations.WmtSecurityMapping;
import com.robert.ernst.account.dto.Account;
import com.robert.ernst.account.dto.ResponseMessage;
import com.robert.ernst.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/get-account/{id}")
    @WmtSecurityMapping(path = "account.get-account")
    public Mono<Account> getAccount(@PathVariable Long id){
        return accountService.getAccountById(id);
    }

    @GetMapping("/get-account-by-username/{username}")
    @WmtSecurityMapping(path = "account.get-account-by-username")
    public Mono<Account> getAccountByUsername(@PathVariable String username){
        return accountService.getAccountByUsername(username);
    }

    @PutMapping("/update-account")
    @WmtSecurityMapping(path = "account.update-account")
    public Mono<ResponseMessage<String>> updateAccount(@Validated @RequestBody Account account) {
        return accountService.updateAccount(account)
                .flatMap(res -> Mono.just(new ResponseMessage<>("User updated successfully")));
    }

    @DeleteMapping("/delete-account/{id}/{deleteId}")
    @WmtSecurityMapping(path = "account.delete-account")
    public Mono<ResponseMessage<String>> deleteAccount(@PathVariable Long id, @PathVariable Long deleteId) {
        return accountService.deleteAccount(id, deleteId)
                .then(Mono.just(new ResponseMessage<>("Account deleted successfully")));
    }

    @GetMapping("/search-by-term/{term}")
    @WmtSecurityMapping(path = "account.search-by-term")
    public Flux<Account> searchByTerm(@PathVariable String term){
        return accountService.getAccountsByTerm(term);
    }
}
