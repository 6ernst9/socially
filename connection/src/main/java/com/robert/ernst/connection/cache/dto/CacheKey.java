package com.robert.ernst.connection.cache.dto;

public record CacheKey(Long id, Long userId) {
    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(CacheKey.class)
                && ((CacheKey) obj).id.equals(this.id)
                && ((CacheKey) obj).userId.equals(this.userId);
    }
}
