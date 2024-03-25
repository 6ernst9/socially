package com.robert.ernst.api_gateway.service;

import com.robert.ernst.api_gateway.configuration.MicroserviceFactory;
import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@AllArgsConstructor
public class GatewayService {
    private final WebClient webClient;
    private final MicroserviceFactory microserviceFactory;

    @Timed(value = "backend.request")
    public Mono<?> get(String path){
        var url = microserviceFactory.getUrl(path);

        var webRequest = webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON);

        return handleResponse(webRequest);
    }

    @Timed(value = "backend.request")
    public Mono<?> post(String path, Object body){
        var url = microserviceFactory.getUrl(path);

        var webRequest = webClient.post()
                .uri(url)
                .body(Mono.just(body), Object.class)
                .accept(MediaType.APPLICATION_JSON);

        return handleResponse(webRequest);
    }

    @Timed(value = "backend.request")
    public Mono<?> update(String path, Object body){
        var url = microserviceFactory.getUrl(path);

        var webRequest = webClient.put()
                .uri(url)
                .body(Mono.just(body), Object.class)
                .accept(MediaType.APPLICATION_JSON);

        return handleResponse(webRequest);
    }

    @Timed(value = "backend.request")
    public Mono<?> delete(String path){
        var url = microserviceFactory.getUrl(path);

        var webRequest = webClient.delete()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON);

        return handleResponse(webRequest);
    }

    private Mono<?> handleResponse(WebClient.RequestHeadersSpec<?> request) {
        return request.exchangeToMono(response -> {
            if (response.statusCode() == HttpStatus.OK) {
                return response.bodyToMono(String.class).flatMap(Mono::just);
            } else {
                return response.bodyToMono(String.class)
                        .flatMap(err -> {
                            log.error("Request failed: {}", err);
                            return Mono.error(new RuntimeException("Request failed:" + err));
                        });
            }
        });
    }
}
