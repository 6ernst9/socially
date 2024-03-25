package com.robert.ernst.api_gateway.configuration;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class MicroserviceFactory {
    private MicroservicesProperties microservicesProperties;
    private Map<String, String> microservicesUrl;

    @PostConstruct
    public void init(){
        microservicesProperties
                .getMicroservices()
                .forEach(microservice -> microservicesUrl.put(microservice.name(), microservice.baseUrl()));
    }

    public String getUrl(String path) {
        String[] parts = path.split("\\.", 2);
        return microservicesUrl.get(parts[0]) + parts[1];
    }
}
