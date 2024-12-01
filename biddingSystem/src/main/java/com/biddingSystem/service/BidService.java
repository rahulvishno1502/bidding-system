package com.biddingSystem.service;

import com.biddingSystem.Entity.User;
import com.biddingSystem.dto.BidDTO;
import com.biddingSystem.exceptions.InvalidBidException;

public interface BidService {
    public void placeBid(BidDTO bidDTO)throws InvalidBidException;
    public User determineWinner(Long productId)throws InvalidBidException;
}
