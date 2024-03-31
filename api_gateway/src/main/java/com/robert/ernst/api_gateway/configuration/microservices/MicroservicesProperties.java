package com.robert.ernst.api_gateway.configuration.microservices;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "backend")
@Data
public class MicroservicesProperties {
    private List<Microservice> microservices;

    public record Microservice(String name, String baseUrl){
    }
}
