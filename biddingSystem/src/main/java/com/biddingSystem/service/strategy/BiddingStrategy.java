package com.biddingSystem.service.strategy;

import com.biddingSystem.Entity.Product;
import com.biddingSystem.Entity.User;

public interface BiddingStrategy {
    User selectWinner(Product product);
}
