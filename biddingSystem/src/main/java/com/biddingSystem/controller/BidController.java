package com.biddingSystem.controller;

import com.biddingSystem.Entity.User;
import com.biddingSystem.Utils.ApiResponse;
import com.biddingSystem.dto.BidDTO;
import com.biddingSystem.exceptions.InvalidBidException;
import com.biddingSystem.service.BidService;
import com.biddingSystem.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;


import java.time.LocalDateTime;

import static com.biddingSystem.Utils.CommonUtils.BID_SAVED_SUCCESSFULLY;

@RestController
public class BidController {
    private final BidService bidService;
    private final RateLimiterService rateLimiterService;
    private KafkaTemplate<String, BidDTO> kafkaTemplate;

    @Autowired
    BidController(BidService bidService, RateLimiterService rateLimiterService, KafkaTemplate kafkaTemplate){
        this.bidService = bidService;
        this.rateLimiterService = rateLimiterService;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * It places the bid for a particular product.
     * It uses rate limiter to stop excess bid from happening at a given point of time.
     * user can place bid in interval of 5 sec and the current bid should be greater than previous bid.
     * Throws various validation message if bid are not proper.
     * @param bidDTO
     * @return
     */
    @PostMapping("/placeBid")
    public ResponseEntity<ApiResponse> placeBid(@RequestBody BidDTO bidDTO) {
        Long userId = bidDTO.getUserID();
        Long productId = bidDTO.getProductId();

        // Check if this user can place another bid
        if (!rateLimiterService.tryAcquireForUser(userId, productId)) {
            ApiResponse response = new ApiResponse(false, "Too many bids from this user. Please wait.", null);
            return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);
        }

        // Check if we can accept more concurrent bids
        if (!rateLimiterService.tryAcquireSemaphore()) {
            ApiResponse response = new ApiResponse(false, "Too many users placing bids at the same time. Try again later.", null);
            return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);
        }

        try {
            bidDTO.setTime(LocalDateTime.now());
            bidService.placeBid(bidDTO);
            ApiResponse response = new ApiResponse(true, BID_SAVED_SUCCESSFULLY, null);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (InvalidBidException i) {
            ApiResponse response = new ApiResponse(false, i.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, "Failed to place the bid. Try Again!", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            // Release the semaphore after processing
            rateLimiterService.releaseSemaphore();
        }
    }

    /**
     * This api is used in determining the winner for the respective AVAILABLE product.
     * If the bid is still going on, it gives the respective validation message for the same.
     * @param productId
     * @return the Winner (User)
     */
    @GetMapping("/winner/{productId}")
    public ResponseEntity<ApiResponse> getWinner(@PathVariable Long productId) {
        try{
            User winner = bidService.determineWinner(productId);
            ApiResponse response = new ApiResponse(true, "Winner found Successfully",winner);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (InvalidBidException e){
            ApiResponse response = new ApiResponse(false, e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, "Failed to find the Winner. Try again !", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//
//    @PostMapping("/placeBid")
//    public String placeBidtoKafka(@RequestBody BidDTO bidDTO) {
//        kafkaTemplate.send("Bid_queue", bidDTO);
//        return "Bid placed successfully";
//    }
}
