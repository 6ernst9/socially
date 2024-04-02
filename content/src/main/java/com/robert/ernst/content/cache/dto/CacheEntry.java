package com.robert.ernst.content.cache.dto;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

public class CacheEntry {
    @Getter
    private final Object value;
    private final Instant expiry;

    public CacheEntry(Object value) {
        this.value = value;
        this.expiry = Instant.now().plus(Duration.ofHours(3));
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiry);
    }
}
