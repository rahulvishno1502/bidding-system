package com.biddingSystem;

import com.biddingSystem.Enums.ProductStatus;
import com.biddingSystem.Utils.CommonUtils;
import com.biddingSystem.Validations.BidValidator;
import com.biddingSystem.dto.BidDTO;
import com.biddingSystem.exceptions.InvalidBidException;
import com.biddingSystem.mapper.EntityMapper;
import com.biddingSystem.Entity.Bid;
import com.biddingSystem.Entity.Product;
import com.biddingSystem.Entity.User;
import com.biddingSystem.repository.BidRepository;
import com.biddingSystem.repository.ProductRepository;
import com.biddingSystem.repository.UserRepository;
import com.biddingSystem.service.BidServiceImpl;
import com.biddingSystem.service.NotificationServiceImpl;
import com.biddingSystem.service.strategy.BiddingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BidServiceImplTest {

    @Mock
    private BiddingStrategy biddingStrategy;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EntityMapper entityMapper;

    @Mock
    private BidValidator bidValidator;

    @Mock
    private NotificationServiceImpl notificationServiceImpl;

    @InjectMocks
    private BidServiceImpl bidService;

    private Product product;
    private User user;
    private BidDTO bidDTO;
    private Bid bid;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product();
        product.setId(1L);
        product.setBasePrice(BigDecimal.valueOf(100));
        product.setSlotEndTime(LocalDateTime.now().minusMinutes(1));
        product.setProductStatus(ProductStatus.AVAILABLE);

        user = new User();
        user.setId(1L);
        user.setEmailId("test@example.com");

//        bidDTO = BidDTO.builder()
//                .productId(1L)
//                .userID(1L)
//                .bidAmount(BigDecimal.valueOf(150))
//                .time(LocalDateTime.now())
//                .build();

        bidDTO= new BidDTO();
        bidDTO.setProductId(1L);
        bidDTO.setUserID(1L);
        bidDTO.setBidAmount(BigDecimal.valueOf(150));
        bidDTO.setTime(LocalDateTime.now());

        bid = new Bid();
        bid.setProduct(product);
        bid.setUser(user);
        bid.setPrice(BigDecimal.valueOf(150));

        when(entityMapper.getProductIdVsProduct()).thenReturn(Map.of(product.getId(), product));
        when(entityMapper.getUserIdVsUser()).thenReturn(Map.of(user.getId(), user));
        when(entityMapper.getProductBidList()).thenReturn(new HashMap<>());
    }

    @Test
    void testPlaceBid_ShouldSaveBidSuccessfully() throws InvalidBidException {
        try (MockedStatic<CommonUtils> mockedCommonUtils = mockStatic(CommonUtils.class)) {
            mockedCommonUtils.when(() -> CommonUtils.isBidAmountLessThanBasePrice(product, bidDTO)).thenReturn(false);
            mockedCommonUtils.when(() -> CommonUtils.isBidNotStarted(product, bidDTO)).thenReturn(false);
            mockedCommonUtils.when(() -> CommonUtils.hasBiddingEnded(product, bidDTO)).thenReturn(false);

            bidService.placeBid(bidDTO);

            verify(bidRepository, times(1)).save(any(Bid.class));
            assertTrue(entityMapper.getProductBidList().containsKey(product.getId()));
        }
    }

    @Test
    void testPlaceBid_BidAmountLessThanBasePrice_ShouldThrowException() throws InvalidBidException {
        doThrow(new InvalidBidException(CommonUtils.BID_AMOUNT_LESS_THANK_BASE_PRICE))
                .when(bidValidator).validateBidAgainstBasePrice(product, bidDTO);
        assertThrows(InvalidBidException.class, () -> bidService.placeBid(bidDTO));

        verify(bidRepository, never()).save(any(Bid.class));
    }


    @Test
    void testDetermineWinner_ShouldReturnWinnerAndNotify() throws InvalidBidException {
        product.setSlotEndTime(LocalDateTime.now().minusHours(1));

        when(biddingStrategy.selectWinner(product)).thenReturn(user);

        User winner = bidService.determineWinner(product.getId());

        verify(userRepository, times(1)).save(user);
        verify(productRepository, times(1)).save(product);

        verify(notificationServiceImpl, times(1)).notifyWinner(user, product);
        verify(notificationServiceImpl, times(1)).notifyOtherUsers(user, product);

        assertEquals(user, winner);
        assertEquals(ProductStatus.SOLD, product.getProductStatus());
    }

}