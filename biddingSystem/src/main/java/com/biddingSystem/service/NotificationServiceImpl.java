package com.biddingSystem.service;

import com.biddingSystem.dto.BidDTO;
import com.biddingSystem.mapper.EntityMapper;
import com.biddingSystem.Entity.Product;
import com.biddingSystem.Entity.User;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class NotificationServiceImpl implements NotificationService{

    private final EntityMapper entityMapper;
    private final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    NotificationServiceImpl(EntityMapper mapper){
        this.entityMapper = mapper;
    }

    public void notifyWinner(User winner, Product product) {
        sendEmailToWinner(winner, product);
    }

    private void sendEmailToWinner(User winner, Product product) {
        logger.info("Sending email to user: {} with message: 'Congratulations, you've won the bid for product: {}!'", winner.getEmailId(), product.getName());
    }

    public void notifyOtherUsers(User winnerUser, Product product){
        List<BidDTO> bidDtos = entityMapper.getProductBidList().get(product.getId());
        List<User> users = bidDtos.stream().map(BidDTO::getUserID).map(id -> entityMapper.getUserIdVsUser().get(id))
                .distinct()
                .filter(user -> winnerUser.getId() != user.getId())
                .toList();
        users.forEach(user -> sendBidEndMail(user.getEmailId(), product));
    }

    private void sendBidEndMail(String toEmail, Product product){
        logger.info("Sending email to user: {} with message: 'Unfortunately, you did not win the bid for product: {}. Better luck next time!'", toEmail, product.getName());

    }
}
