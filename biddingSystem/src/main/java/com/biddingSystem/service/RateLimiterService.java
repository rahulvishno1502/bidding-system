package com.biddingSystem.service;

public interface RateLimiterService {
    public boolean tryAcquireForUser(Long userId, Long productId);
    public boolean tryAcquireSemaphore();
    public void releaseSemaphore();
}
