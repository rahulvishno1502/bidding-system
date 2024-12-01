package com.biddingSystem.service;

import com.biddingSystem.Enums.ProductStatus;
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
import com.biddingSystem.service.strategy.BiddingStrategy;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.biddingSystem.Utils.CommonUtils.*;

@Service
public class BidServiceImpl implements BidService{

    private final BiddingStrategy biddingStrategy;
    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;
    private final NotificationService notificationService;
    private final ProductRepository productRepository;
    private final BidValidator bidValidator;
    private final KafkaTemplate kafkaTemplate;

    Logger logger = LoggerFactory.getLogger(BidServiceImpl.class);

    @Autowired
    public BidServiceImpl(BidRepository bidRepository, UserRepository userRepository, EntityMapper mapper, NotificationService notificationService, BiddingStrategy biddingStrategy, ProductRepository productRepository, BidValidator bidValidator, KafkaTemplate kafkaTemplate) {
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.entityMapper = mapper;
        this.notificationService = notificationService;
        this.biddingStrategy = biddingStrategy;
        this.productRepository = productRepository;
        this.bidValidator = bidValidator;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void fillEntityMapper(){
        fillProductIdVsProductMap(productRepository, entityMapper);
        fillUserIdVsUserMap(userRepository, entityMapper);
        fillProductBidList(bidRepository, entityMapper);
        logger.info("All records fetched Successfully");
        logger.info("No. of users - {}", entityMapper.getUserIdVsUser().size());
        logger.info("No. of products - {}", entityMapper.getProductIdVsProduct().size());
    }

    public void placeBid(BidDTO bidDTO) throws InvalidBidException {
        Product product = entityMapper.getProductIdVsProduct().get(bidDTO.getProductId());
        validateBids(product, bidDTO);
        try {
            kafkaTemplate.send("Bid_queue", bidDTO);
        }catch (Exception e){
            logger.info(e.getMessage());
        }
    }

    @KafkaListener(topics = "Bid_queue", groupId = "bid_group")
    private void saveBid(BidDTO bidDTO){
        Product product = entityMapper.getProductIdVsProduct().get(bidDTO.getProductId());
        saveBid(bidDTO, product);
    }

    private void validateBids(Product product , BidDTO bidDTO) throws InvalidBidException {
        bidValidator.validateBidAmount(bidDTO);
        bidValidator.validateBidAgainstBasePrice(product, bidDTO);
        bidValidator.validateBidStart(product, bidDTO);
        bidValidator.validateBidEnd(product, bidDTO);
        bidValidator.validateUserBidHistory(bidDTO, entityMapper, product);
    }

    private void saveBid(BidDTO bidDTO, Product product) {
        Bid bid = new Bid();
        bid.setProduct(product);
        bid.setUser(entityMapper.getUserIdVsUser().get(bidDTO.getUserID()));
        bid.setPrice(bidDTO.getBidAmount());
        bid.setTime(bidDTO.getTime());

        entityMapper.getProductBidList()
                        .computeIfAbsent(product.getId(), k -> new ArrayList<>())
                        .add(bidDTO);

        bidRepository.save(bid);
        logger.info("Bid Saved Successfully.");
    }

    public User determineWinner(Long productId) throws InvalidBidException {
        Product product = entityMapper.getProductIdVsProduct().get(productId);

        if(product.getProductStatus().equals(ProductStatus.SOLD)) {
            return  entityMapper.getUserIdVsUser().get(product.getOwnerId());
        }
        bidValidator.validateBidStart(product);
        bidValidator.validateBidStillGoingOn(product);

        User winner = biddingStrategy.selectWinner(product);
        if(winner == null)return null;
        saveWinnerDetails(winner, product);
        notifyBidders(winner, product);
        return winner;
    }


    private void saveWinnerDetails(User winner, Product product) {
        List<Product> boughtProductList = winner.getBoughtProducts();
        if(Objects.isNull(boughtProductList))boughtProductList = new ArrayList<>();
        boughtProductList.add(product);
        winner.setBoughtProducts(boughtProductList);

        userRepository.save(winner);
        product.setProductStatus(ProductStatus.SOLD);
        product.setOwnerId(winner.getId());
        productRepository.save(product);
        logger.info("Winner determined successfully");
    }

    private void notifyBidders(User winner, Product product) {
        notificationService.notifyWinner(winner, product);
        notificationService.notifyOtherUsers(winner, product);
    }

}
