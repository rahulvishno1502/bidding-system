package com.biddingSystem.service;

import com.biddingSystem.Entity.Product;
import com.biddingSystem.Entity.User;

public interface NotificationService {
    public void notifyWinner(User winner, Product product);
    public void notifyOtherUsers(User winnerUser, Product product);
}
