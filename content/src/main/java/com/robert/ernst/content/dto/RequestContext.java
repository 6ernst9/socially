package com.robert.ernst.content.dto;

import java.util.Map;

public class RequestContext {
    private static final ThreadLocal<Map<String, String>> requestHeaders = new ThreadLocal<>();

    public static void setHeaders(Map<String, String> headers) {
        requestHeaders.set(headers);
    }

    public static Map<String, String> getHeaders() {
        return requestHeaders.get();
    }
}
