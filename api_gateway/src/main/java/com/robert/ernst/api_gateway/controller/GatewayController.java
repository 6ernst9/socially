package com.robert.ernst.api_gateway.controller;

import com.robert.ernst.api_gateway.dto.ReqBody;
import com.robert.ernst.api_gateway.service.GatewayService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GatewayController {
    private final GatewayService gatewayService;

    @Timed
    @GetMapping
    public Mono<Object> get(@RequestBody ReqBody request) {
        log.info("Received GET request to {}", request.path());
        return gatewayService.get(request.path());
    }

    @Timed
    @PostMapping
    public Mono<Object> post(@RequestBody ReqBody request) {
        log.info("Received POST request to {}, with body: {}", request.path(), request.body());
        return gatewayService.post(request.path(), request.body());
    }

    @Timed
    @PutMapping
    public Mono<Object> update(@RequestBody ReqBody request) {
        log.info("Received PUT request to {}, with body: {}", request.path(), request.body());
        return gatewayService.update(request.path(), request.body());
    }

    @Timed
    @DeleteMapping
    public Mono<Object> delete(@RequestBody ReqBody request) {
        log.info("Received PUT request to {}", request.path());
        return gatewayService.delete(request.path());
    }
}
