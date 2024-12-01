
package com.biddingSystem;


import com.biddingSystem.dto.BidDTO;
import com.biddingSystem.mapper.EntityMapper;
import com.biddingSystem.Entity.Product;
import com.biddingSystem.Entity.User;
import com.biddingSystem.service.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class NotificationServiceImplTest {

    @Mock
    private EntityMapper entityMapper;

    @Mock
    private Logger logger;  // Mock the logger

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User winner;
    private Product product;
    private List<BidDTO> bidDTOList;
    private List<User> users;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inject mock logger into the NotificationServiceImpl
        ReflectionTestUtils.setField(notificationService, "logger", logger);

        // Initialize sample data for testing
        winner = new User();
        winner.setId(1);
        winner.setEmailId("winner@example.com");

        product = new Product();
        product.setId(100L);
        product.setName("Test Product");

        // Set up other users who participated in the bidding
        users = new ArrayList<>();
        User user1 = new User();
        user1.setId(2);
        user1.setEmailId("user1@example.com");
        users.add(user1);

        User user2 = new User();
        user2.setId(3);
        user2.setEmailId("user2@example.com");
        users.add(user2);

        // Set up mock BidDTOs
        bidDTOList = new ArrayList<>();
        BidDTO bid1 = BidDTO.builder().build();
        bid1.setUserID(2L);
        bidDTOList.add(bid1);

        BidDTO bid2 = BidDTO.builder().build();
        bid2.setUserID(3L);
        bidDTOList.add(bid2);

        when(entityMapper.getProductBidList()).thenReturn(Map.of(product.getId(), bidDTOList));
        when(entityMapper.getUserIdVsUser()).thenReturn(Map.of(
                2L, user1,
                3L, user2
        ));
    }

    @Test
    void notifyWinner_ShouldSendEmailToWinner() {
        notificationService.notifyWinner(winner, product);
        verify(logger).info("Sending email to user: {} with message: 'Congratulations, you've won the bid for product: {}!'",
                winner.getEmailId(), product.getName());
    }

    @Test
    void notifyOtherUsers_ShouldSendEmailsToOtherUsers() {
        notificationService.notifyOtherUsers(winner, product);
        verify(logger, times(1)).info("Sending email to user: {} with message: 'Unfortunately, you did not win the bid for product: {}. Better luck next time!'",
                "user1@example.com", product.getName());
        verify(logger, times(1)).info("Sending email to user: {} with message: 'Unfortunately, you did not win the bid for product: {}. Better luck next time!'",
                "user2@example.com", product.getName());

        verify(logger, never()).info(anyString(), eq(winner.getEmailId()), anyString());
    }
}