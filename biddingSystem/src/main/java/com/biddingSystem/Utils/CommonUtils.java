package com.biddingSystem.Utils;

import com.biddingSystem.Enums.ProductStatus;
import com.biddingSystem.dto.BidDTO;
import com.biddingSystem.mapper.EntityMapper;
import com.biddingSystem.Entity.Bid;
import com.biddingSystem.Entity.Product;
import com.biddingSystem.Entity.User;
import com.biddingSystem.repository.BidRepository;
import com.biddingSystem.repository.ProductRepository;
import com.biddingSystem.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CommonUtils {

    public static boolean isNotValidTimeSlot(Product product) {
        return product.getSlotStartTime().isBefore(LocalDateTime.now()) || product.getSlotEndTime().isBefore(product.getSlotStartTime());
    }

    public static boolean isBidAmountLessThanBasePrice(Product product, BidDTO bidDTO){
        return bidDTO.getBidAmount().compareTo(product.getBasePrice()) < 0;
    }

    public static boolean isBidNotStarted(Product product, BidDTO bidDTO) {
        return bidDTO.getTime().isBefore(product.getSlotStartTime());
    }

    public static boolean hasBiddingEnded(Product product, BidDTO bidDTO) {
        return bidDTO.getTime().isAfter(product.getSlotEndTime());
    }

    public static void fillProductBidList(BidRepository bidRepository, EntityMapper entityMapper) {
        List<Bid> bidList = bidRepository.findAll();
        for(Bid bid : bidList){
            if(!ProductStatus.AVAILABLE.equals(bid.getProduct().getProductStatus()))continue;
            List<BidDTO> bidDTOList = entityMapper.getProductBidList().getOrDefault(bid.getProduct().getId(), new ArrayList<>());

            BidDTO bidDTO = new BidDTO();
            bidDTO.setBidAmount(bid.getPrice());
            bidDTO.setProductId(bid.getProduct().getId());
            bidDTO.setTime(bid.getTime());
            bidDTO.setUserID(bid.getUser().getId());

            bidDTOList.add(bidDTO);
            entityMapper.getProductBidList().put(bid.getProduct().getId(), bidDTOList);
        }
    }

    public static void fillUserIdVsUserMap(UserRepository userRepository, EntityMapper entityMapper) {
        List<User> userList = userRepository.findAll();
        userList.forEach(user -> entityMapper.getUserIdVsUser().put(user.getId(), user));
    }

    public static void fillProductIdVsProductMap(ProductRepository productRepository, EntityMapper entityMapper) {
        List<Product> productList = productRepository.findAll();
        productList.forEach(prod -> entityMapper.getProductIdVsProduct().put(prod.getId(), prod));
    }


    public static final String BID_SAVED_SUCCESSFULLY = "Your Bid is placed successfully.";
    public static final String BID_AMOUNT_LESS_THANK_BASE_PRICE = "Bid Amount is less than the Base Price.";

    public static final String BID_NOT_STARTED = "Bidding has not started.";
    public static final String BID_IS_ENDED = "Bidding has ended.";
    public static final String BID_AMOUNT_LESS_THAN_PREVIOUS_BID = "Bid amount cannot be less or equla to previous bid amount. Try again.";
    public static final String BID_AMOUNT_LESS_THAN_ZERO = "Bid amount cannot be less than 0.";
    public static final String BIDDING_IS_STILL_GOING_ON = "Bidding is still Going on !";


    public static boolean hasSameUserHadBidMore(BidDTO bidDTO, EntityMapper entityMapper, Product product) {
        List<BidDTO> bidList = entityMapper.getProductBidList().get(product.getId());
        if(Objects.isNull(bidList) || bidList.isEmpty())return false;
        List<BidDTO> userBidHistory = new ArrayList<>(bidList.stream()
                .filter(bidDTO1 -> Objects.equals(bidDTO1.getUserID(), bidDTO.getUserID())).toList());
        userBidHistory.sort(Comparator.comparing(BidDTO::getBidAmount).reversed());
        if(userBidHistory.isEmpty())return false;
        return bidDTO.getBidAmount().compareTo(userBidHistory.get(0).getBidAmount()) <= 0;
    }
}
