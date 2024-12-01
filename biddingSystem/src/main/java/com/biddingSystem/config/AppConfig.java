package com.biddingSystem.config;

import com.biddingSystem.service.strategy.BiddingStrategy;
import com.biddingSystem.service.strategy.HighestBidWins;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This helps in configuring the strategy used
 * in determining the winner
 */
@Configuration
public class AppConfig {
    @Bean
    public BiddingStrategy biddingStrategy() {
        return new HighestBidWins();
    }
}
