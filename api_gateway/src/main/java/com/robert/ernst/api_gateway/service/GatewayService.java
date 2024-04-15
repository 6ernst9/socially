package com.robert.ernst.api_gateway.service;

import com.robert.ernst.api_gateway.configuration.microservices.MicroserviceFactory;
import com.robert.ernst.api_gateway.configuration.SecurityProperties;
import com.robert.ernst.api_gateway.dto.RequestContext;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class GatewayService {
    private final WebClient webClient;
    private final MicroserviceFactory microserviceFactory;
    private final SecurityProperties securityProperties;

    @Timed(value = "backend.request")
    public Mono<Object> get(String path){
        var headers = RequestContext.getHeaders();
        var url = microserviceFactory.getUrl(URLDecoder.decode(path, StandardCharsets.UTF_8));

        var webRequest = webClient.get()
                .uri(url)
                .headers(httpHeaders -> headers.forEach(httpHeaders::set))
                .header("clientId", securityProperties.getClientId())
                .header("clientSecret", securityProperties.getClientSecret())
                .header("X-FI-SY-PATH", path)
                .accept(MediaType.APPLICATION_JSON);

        return handleResponse(webRequest);
    }

    @Timed(value = "backend.request")
    public Mono<Object> post(String path, Object body){
        var headers = RequestContext.getHeaders();
        var url = microserviceFactory.getUrl(path);

        var webRequest = webClient.post()
                .uri(url)
                .headers(httpHeaders -> headers.forEach(httpHeaders::set))
                .header("clientId", securityProperties.getClientId())
                .header("clientSecret", securityProperties.getClientSecret())
                .header("X-FI-SY-PATH", path)
                .body(Mono.just(body), Object.class)
                .accept(MediaType.APPLICATION_JSON);

        return handleResponse(webRequest);
    }

    @Timed(value = "backend.request")
    public Mono<Object> update(String path, Object body){
        var headers = RequestContext.getHeaders();
        var url = microserviceFactory.getUrl(path);

        var webRequest = webClient.put()
                .uri(url)
                .headers(httpHeaders -> headers.forEach(httpHeaders::set))
                .header("clientId", securityProperties.getClientId())
                .header("clientSecret", securityProperties.getClientSecret())
                .header("X-FI-SY-PATH", path)
                .body(Mono.just(body), Object.class)
                .accept(MediaType.APPLICATION_JSON);

        return handleResponse(webRequest);
    }

    @Timed(value = "backend.request")
    public Mono<Object> delete(String path){
        var headers = RequestContext.getHeaders();
        var url = microserviceFactory.getUrl(path);

        var webRequest = webClient.delete()
                .uri(url)
                .headers(httpHeaders -> headers.forEach(httpHeaders::set))
                .header("clientId", securityProperties.getClientId())
                .header("clientSecret", securityProperties.getClientSecret())
                .header("X-FI-SY-PATH", path)
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
