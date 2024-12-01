package com.biddingSystem.Validations;

import com.biddingSystem.Entity.Product;
import com.biddingSystem.Utils.CommonUtils;
import com.biddingSystem.dto.BidDTO;
import com.biddingSystem.exceptions.InvalidBidException;
import com.biddingSystem.mapper.EntityMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.biddingSystem.Utils.CommonUtils.*;

@Component
public class BidValidator {
    public void validateBidAmount(BidDTO bidDTO) throws InvalidBidException {
        if (bidDTO.getBidAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidBidException(BID_AMOUNT_LESS_THAN_ZERO);
        }
    }

    public void validateBidAgainstBasePrice(Product product, BidDTO bidDTO) throws InvalidBidException {
        if (CommonUtils.isBidAmountLessThanBasePrice(product, bidDTO)) {
            throw new InvalidBidException(BID_AMOUNT_LESS_THANK_BASE_PRICE);
        }
    }

    public void validateBidStart(Product product, BidDTO bidDTO) throws InvalidBidException {
        if (CommonUtils.isBidNotStarted(product, bidDTO)) {
            throw new InvalidBidException(BID_NOT_STARTED);
        }
    }
    public void validateBidStart(Product product) throws InvalidBidException {
        if (product.getSlotStartTime().isAfter(LocalDateTime.now())) {
            throw new InvalidBidException(BID_NOT_STARTED);
        }
    }

    public void validateBidEnd(Product product, BidDTO bidDTO) throws InvalidBidException {
        if (CommonUtils.hasBiddingEnded(product, bidDTO)) {
            throw new InvalidBidException(BID_IS_ENDED);
        }
    }

    public void validateUserBidHistory(BidDTO bidDTO, EntityMapper entityMapper, Product product) throws InvalidBidException {
        if (CommonUtils.hasSameUserHadBidMore(bidDTO, entityMapper, product)) {
            throw new InvalidBidException(BID_AMOUNT_LESS_THAN_PREVIOUS_BID);
        }
    }

    public void validateBidStillGoingOn(Product product) throws InvalidBidException {
        if(product.getSlotEndTime().isAfter(LocalDateTime.now()))throw new InvalidBidException(BIDDING_IS_STILL_GOING_ON);
    }
}
