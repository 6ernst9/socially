package com.robert.ernst.account.config;

import com.robert.ernst.account.config.annotations.WmtSecurityMapping;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class SecurityAspect {
    @Value("${security.client-id}")
    private String clientId;
    @Value("${security.client-secret}")
    private String clientSecret;

    @Before("@annotation(mapping)")
    public void securityFilterChain(WmtSecurityMapping mapping){
        var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        var requestClientId = request.getHeader("clientId");
        var requestClientSecret = request.getHeader("clientSecret");
        var authorization = request.getHeader("Authorization");

        var requestMapping = request.getHeader("X-FI-SY-PATH");
        var siteId = request.getHeader("X-FI-SY-SITE-ID");
        var device = request.getHeader("X-FI-SY-DEVICE");
        var ipAddress = request.getHeader("X-FI-SY-IP");

        Assert.notNull(requestClientId, "clientId cannot be null");
        Assert.notNull(requestClientSecret, "clientSecret cannot be null");
        if(!requestMapping.contains("login") && !requestMapping.contains("register") && !requestMapping.contains("logout")){
            Assert.notNull(authorization, "authorization cannot be null");

            var token = authorization.substring(7);
            if(!authorization.startsWith("Bearer") || token.length() < 120) {
                throw new SecurityException("Invalid authorization");
            }
        }

        if (!clientId.equals(requestClientId) || !clientSecret.equals(requestClientSecret)) {
            throw new SecurityException("Invalid clientId or secret");
        }

        Assert.notNull(requestMapping, "path cannot be null");
        Assert.notNull(siteId, "siteId cannot be null");
        Assert.notNull(device, "device cannot be null");
        Assert.notNull(ipAddress, "ipAddress cannot be null");

        if(!requestMapping.contains(mapping.path())){
            throw new SecurityException("Invalid request mapping");
        }
    }
}
