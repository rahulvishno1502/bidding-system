package com.biddingSystem;

import com.biddingSystem.Entity.Product;
import com.biddingSystem.Utils.CommonUtils;
import com.biddingSystem.dto.BidDTO;
import com.biddingSystem.exceptions.InvalidBidException;
import com.biddingSystem.mapper.EntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.biddingSystem.Validations.BidValidator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BidValidatorTest {

    @InjectMocks
    private BidValidator bidValidator;

    @Mock
    private EntityMapper entityMapper;

    @Mock
    private Product product;

    @Mock
    private BidDTO bidDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateBidAmount_ShouldThrowException_WhenBidAmountLessThanZero() {
        // Arrange
        when(bidDTO.getBidAmount()).thenReturn(BigDecimal.valueOf(-1));

        // Act & Assert
        assertThrows(InvalidBidException.class, () -> bidValidator.validateBidAmount(bidDTO),
                CommonUtils.BID_AMOUNT_LESS_THAN_ZERO);
    }


    @Test
    void validateBidStart_ShouldThrowException_WhenBidBeforeStart() {
        // Arrange
        when(product.getSlotStartTime()).thenReturn(LocalDateTime.now().plusMinutes(10)); // Return LocalDateTime
        when(bidDTO.getTime()).thenReturn(LocalDateTime.now());

        // Act & Assert
        assertThrows(InvalidBidException.class, () -> bidValidator.validateBidStart(product, bidDTO),
                CommonUtils.BID_NOT_STARTED);
    }


    @Test
    void validateUserBidHistory_ShouldThrowException_WhenBidLessThanPrevious() {
        // Arrange
        // Mock product ID and user ID
        when(product.getId()).thenReturn(100L);  // Ensure it returns Long
        when(bidDTO.getUserID()).thenReturn(1L);  // Ensure it returns Long

        // Mock the product bid list in entityMapper
        BidDTO previousBid = BidDTO.builder()
                .bidAmount(BigDecimal.valueOf(200))
                .userID(1L)
                .build();

        // Assume user has already placed a bid of 200
        List<BidDTO> bidList = List.of(previousBid);
        when(entityMapper.getProductBidList()).thenReturn(Map.of(100L, bidList));

        // Mock that the new bid is less than the previous one
        when(bidDTO.getBidAmount()).thenReturn(BigDecimal.valueOf(150));

        // Act & Assert
        assertThrows(InvalidBidException.class, () -> bidValidator.validateUserBidHistory(bidDTO, entityMapper, product),
                CommonUtils.BID_AMOUNT_LESS_THAN_PREVIOUS_BID);
    }


    @Test
    void validateBidStillGoingOn_ShouldThrowException_WhenBiddingStillActive() {
        // Arrange
        when(product.getSlotEndTime()).thenReturn(LocalDateTime.now().plusMinutes(10));  // Return LocalDateTime

        // Act & Assert
        assertThrows(InvalidBidException.class, () -> bidValidator.validateBidStillGoingOn(product),
                CommonUtils.BIDDING_IS_STILL_GOING_ON);
    }
}


