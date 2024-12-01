package com.biddingSystem.dto;

import com.biddingSystem.Entity.Product;
import com.biddingSystem.Enums.ProductStatus;
import com.biddingSystem.exceptions.InvalidBidException;
import com.biddingSystem.repository.ProductRepository;
import com.biddingSystem.service.BidService;
import com.biddingSystem.service.BidServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class BiddingScheduler {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BidService bidService;

    Logger logger = LoggerFactory.getLogger(BiddingScheduler.class);

    @Scheduled(fixedRate = 60000) // Run every 1 minute
    public void checkAndDetermineWinners() {
        List<Product> unsoldProducts = productRepository.findByProductStatus(ProductStatus.AVAILABLE);
        logger.info("Scheduler is checking");

        unsoldProducts.forEach(product -> {
            if (LocalDateTime.now().isAfter(product.getSlotEndTime())) {
                try {
                    bidService.determineWinner(product.getId());
                    logger.info("Scheduler determined winner for a product- "+product.getId());
                } catch (InvalidBidException e) {
                    System.err.println("Error determining winner for product ID " + product.getId() + ": " + e.getMessage());
                }
            }
        });
    }
}

