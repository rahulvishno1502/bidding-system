package com.biddingSystem.service;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Service
public class RateLimiterServiceImpl implements RateLimiterService{

    private final Map<String, RateLimiter> userRateLimiters = new ConcurrentHashMap<>();
    private final Semaphore semaphore = new Semaphore(5);
    private final double userBidRateLimit = 1.0 / 5.0;

    public boolean tryAcquireForUser(Long userId, Long productId) {
        String uniqueId = String.valueOf(userId)+"-"+String.valueOf(productId);
        RateLimiter rateLimiter = userRateLimiters.computeIfAbsent(uniqueId, id -> RateLimiter.create(userBidRateLimit));
        return rateLimiter.tryAcquire();
    }

    public boolean tryAcquireSemaphore() {
        return semaphore.tryAcquire();
    }

    public void releaseSemaphore() {
        semaphore.release();
    }
}

