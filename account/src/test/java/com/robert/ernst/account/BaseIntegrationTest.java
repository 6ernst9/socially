package com.robert.ernst.account;

import com.robert.ernst.account.dto.Account;
import com.robert.ernst.account.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {
    @Value("${test.headers.site-id}")
    private String siteId;
    @Value("${test.headers.ip-address}")
    private String ipAddress;
    @Value("${test.headers.device}")
    private String device;
    @Value("${test.headers.token}")
    private String token;
    @Value("${security.client-id}")
    private String clientId;
    @Value("${security.client-secret}")
    private String clientSecret;
    private final String AUTHORIZATION_HEADER = "Bearer %s";
    @Autowired
    protected WebTestClient webTestClient;
    @MockBean
    private AccountsRepository accountsRepository;

    protected void stubFindAllMethod(){
        when(accountsRepository.findAll())
                .thenReturn(Flux.fromIterable(List.of(
                        new Account("mihai123",
                                "mihai123@mock.com",
                                "password123",
                                "Mihai",
                                "07xxxxxxxx"),
                        new Account("cristi123",
                                "cristi123@mock.com",
                                "password123",
                                "Cristi",
                                "07xxxxxxxx"),
                        new Account("robert123",
                                "robert_cris@mock.com",
                                "password123",
                                "Robert Cris",
                                "07xxxxxxxx")
                )));
    }

    protected void stubFindByIdMethod(){
        when(accountsRepository.findById(anyLong()))
                .thenReturn(Mono.just(new Account("username123",
                        "mock_email123@mock.com",
                        "password123",
                        "Jane Doe",
                        "07xxxxxxxx")));
    }

    protected void stubFindByIdMethodError(){
        when(accountsRepository.findById(anyLong()))
                .thenReturn(Mono.empty());
    }

    protected Consumer<HttpHeaders> getDefaultHeaders() {
        return httpHeaders -> {
            httpHeaders.add("clientId", clientId);
            httpHeaders.add("clientSecret", clientSecret);
            httpHeaders.add("X-FI-SY-DEVICE", device);
            httpHeaders.add("X-FI-SY-IP", ipAddress);
            httpHeaders.add("X-FI-SY-SITE-ID", siteId);
            httpHeaders.add("Authorization", AUTHORIZATION_HEADER.formatted(token));
        };
    }
}
