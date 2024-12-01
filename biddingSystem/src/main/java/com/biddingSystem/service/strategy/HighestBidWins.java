package com.biddingSystem.service.strategy;

import com.biddingSystem.dto.BidDTO;
import com.biddingSystem.mapper.EntityMapper;
import com.biddingSystem.Entity.Product;
import com.biddingSystem.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class HighestBidWins implements BiddingStrategy {

    @Autowired
    private EntityMapper entityMapper;

    /**
     * It helps in determining the winner by selecting the bidder with
     * the highest bid amount and quickest time.
     * @param product
     * @return
     */
    @Override
    public User selectWinner(Product product) {
        List<BidDTO> bidDTOList = entityMapper.getProductBidList().get(product.getId());
        if(bidDTOList.isEmpty())return null;
        bidDTOList.sort(Comparator.comparing(BidDTO::getBidAmount).reversed()
                .thenComparing(BidDTO::getTime));

        return entityMapper.getUserIdVsUser().get(bidDTOList.get(0).getUserID());
    }
}
