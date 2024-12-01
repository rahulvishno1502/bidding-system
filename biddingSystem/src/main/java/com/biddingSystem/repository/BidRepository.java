package com.biddingSystem.repository;

import com.biddingSystem.Entity.Bid;
import com.biddingSystem.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByProduct(Product product);
}
