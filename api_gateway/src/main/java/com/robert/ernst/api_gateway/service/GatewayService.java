package com.robert.ernst.api_gateway.service;

import com.robert.ernst.api_gateway.configuration.microservices.MicroserviceFactory;
import com.robert.ernst.api_gateway.configuration.SecurityProperties;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class GatewayService {
    private final WebClient webClient;
    private final MicroserviceFactory microserviceFactory;
    private final SecurityProperties securityProperties;
    private final String siteId = "COM";
    private final String ipAddress = "127.0.0";
    private final String device = "mobile";
    private final String AUTHORIZATION_HEADER = "Bearer %s";
    private final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaGSh23zOl21k4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ";

    @Timed(value = "backend.request")
    public Mono<Object> get(String path){
        var url = microserviceFactory.getUrl(path);

        var webRequest = webClient.get()
                .uri(url)
                .header("clientId", securityProperties.getClientId())
                .header("clientSecret", securityProperties.getClientSecret())
                .header("X-FI-SY-PATH", path)
                .header("X-FI-SY-DEVICE", device)
                .header("X-FI-SY-IP", ipAddress)
                .header("X-FI-SY-SITE-ID", siteId)
                .header("Authorization",  AUTHORIZATION_HEADER.formatted(token))
                .accept(MediaType.APPLICATION_JSON);

        return handleResponse(webRequest);
    }

    @Timed(value = "backend.request")
    public Mono<Object> post(String path, Object body){
        var url = microserviceFactory.getUrl(path);

        var webRequest = webClient.post()
                .uri(url)
                .header("clientId", securityProperties.getClientId())
                .header("clientSecret", securityProperties.getClientSecret())
                .header("X-FI-SY-PATH", path)
                .header("X-FI-SY-DEVICE", device)
                .header("X-FI-SY-IP", ipAddress)
                .header("X-FI-SY-SITE-ID", siteId)
                .header("Authorization",  AUTHORIZATION_HEADER.formatted(token))
                .body(Mono.just(body), Object.class)
                .accept(MediaType.APPLICATION_JSON);

        return handleResponse(webRequest);
    }

    @Timed(value = "backend.request")
    public Mono<Object> update(String path, Object body){
        var url = microserviceFactory.getUrl(path);

        var webRequest = webClient.put()
                .uri(url)
                .header("clientId", securityProperties.getClientId())
                .header("clientSecret", securityProperties.getClientSecret())
                .header("X-FI-SY-PATH", path)
                .header("X-FI-SY-DEVICE", device)
                .header("X-FI-SY-IP", ipAddress)
                .header("X-FI-SY-SITE-ID", siteId)
                .header("Authorization",  AUTHORIZATION_HEADER.formatted(token))
                .body(Mono.just(body), Object.class)
                .accept(MediaType.APPLICATION_JSON);

        return handleResponse(webRequest);
    }

    @Timed(value = "backend.request")
    public Mono<Object> delete(String path){
        var url = microserviceFactory.getUrl(path);

        var webRequest = webClient.delete()
                .uri(url)
                .header("clientId", securityProperties.getClientId())
                .header("clientSecret", securityProperties.getClientSecret())
                .header("X-FI-SY-PATH", path)
                .header("X-FI-SY-DEVICE", device)
                .header("X-FI-SY-IP", ipAddress)
                .header("X-FI-SY-SITE-ID", siteId)
                .header("Authorization",  AUTHORIZATION_HEADER.formatted(token))
                .accept(MediaType.APPLICATION_JSON);

        return handleResponse(webRequest);
    }

    private Mono<Object> handleResponse(WebClient.RequestHeadersSpec<?> request) {
        return request.exchangeToMono(response -> {
            if (response.statusCode() == HttpStatus.OK) {
                return response.bodyToMono(Object.class).flatMap(Mono::just);
            } else {
                return response.bodyToMono(Object.class)
                        .flatMap(err -> {
                            log.error("Request failed: {}", err);
                            return Mono.error(new RuntimeException("Request failed:" + err));
                        });
            }
        });
    }
}
