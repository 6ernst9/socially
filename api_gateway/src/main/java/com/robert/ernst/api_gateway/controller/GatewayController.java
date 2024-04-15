package com.robert.ernst.api_gateway.controller;

import com.robert.ernst.api_gateway.dto.GetBody;
import com.robert.ernst.api_gateway.dto.ReqBody;
import com.robert.ernst.api_gateway.service.GatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GatewayController {
    private final GatewayService gatewayService;

    @GetMapping
    public Mono<Object> get(@RequestParam String path) {
        log.info("Received GET request to {}", path);
        return gatewayService.get(path);
    }

    @PostMapping
    public Mono<Object> post(@RequestParam String path, @RequestBody Object body) {
        log.info("Received POST request to {}, with body: {}", path, body);
        return gatewayService.post(path, body);
    }

    @PutMapping
    public Mono<Object> update(@RequestBody ReqBody request) {
        log.info("Received PUT request to {}, with body: {}", request.path(), request.body());
        return gatewayService.update(request.path(), request.body());
    }

    @DeleteMapping
    public Mono<Object> delete(@RequestBody GetBody request) {
        log.info("Received PUT request to {}", request.path());
        return gatewayService.delete(request.path());
    }
}
