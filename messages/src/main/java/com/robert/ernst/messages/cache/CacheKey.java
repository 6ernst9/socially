package com.robert.ernst.messages.cache;

public record CacheKey(Long senderId, Long receiverId) {
    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(CacheKey.class)
                && ((CacheKey) obj).senderId.equals(this.senderId)
                && ((CacheKey) obj).receiverId.equals(this.receiverId);
    }
}
