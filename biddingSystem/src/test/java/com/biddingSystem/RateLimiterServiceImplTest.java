package com.biddingSystem;

import com.biddingSystem.service.RateLimiterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;


import static org.junit.jupiter.api.Assertions.*;

class RateLimiterServiceImplTest {

    @InjectMocks
    private RateLimiterServiceImpl rateLimiterService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTryAcquireForUser_WhenWithinLimit_ShouldAllow() {
        Long userId = 1L;
        Long productId = 1L;
        boolean result = rateLimiterService.tryAcquireForUser(userId, productId);
        assertTrue(result, "User should be able to acquire within rate limit.");
    }

    @Test
    void testTryAcquireForUser_WhenRateLimitExceeded_ShouldBlock() {
        Long userId = 1L;
        Long productId = 1L;
        for (int i = 0; i < 10; i++) {
            rateLimiterService.tryAcquireForUser(userId, productId);
        }

        boolean result = rateLimiterService.tryAcquireForUser(userId, productId);
        assertFalse(result, "User should be blocked after exceeding rate limit.");
    }

    @Test
    void testTryAcquireSemaphore_WhenAvailable_ShouldAllow() {
        boolean result = rateLimiterService.tryAcquireSemaphore();
        assertTrue(result, "Should be able to acquire semaphore when permits are available.");
    }

    @Test
    void testTryAcquireSemaphore_WhenLimitReached_ShouldBlock() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            rateLimiterService.tryAcquireSemaphore();
        }

        boolean result = rateLimiterService.tryAcquireSemaphore();
        assertFalse(result, "Should block when semaphore limit is reached.");
    }

    @Test
    void testReleaseSemaphore_ShouldIncreaseAvailability() throws InterruptedException {
        boolean acquireResult = rateLimiterService.tryAcquireSemaphore();
        assertTrue(acquireResult, "Should be able to acquire semaphore initially.");

        rateLimiterService.releaseSemaphore();
        boolean secondAcquireResult = rateLimiterService.tryAcquireSemaphore();
        assertTrue(secondAcquireResult, "Should be able to acquire semaphore after releasing.");
    }
}

