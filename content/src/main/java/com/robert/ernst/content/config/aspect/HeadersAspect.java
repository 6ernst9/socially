package com.robert.ernst.content.config.aspect;

import com.robert.ernst.content.dto.RequestContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Aspect
@Component
public class HeadersAspect {
    Set<String> wmtHeaders = Set.of("X-FI-SY-IP", "X-FI-SY-DEVICE", "X-FI-SY-SITE-ID", "Authorization");

    @Pointcut("execution(* com.robert.ernst.content.controller.ContentController.*(..))")
    public void controllerMethods() {
    }

    @Before("controllerMethods()")
    public void parseRequestHeaders() {
        var request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        Map<String, String> headers = new HashMap<>();
        wmtHeaders.forEach(header -> {
                    if(request.getHeader(header)!=null){
                        headers.put(header, request.getHeader(header));
                    }
                });

        RequestContext.setHeaders(headers);
    }
}
