package com.biddingSystem.mapper;

import com.biddingSystem.dto.BidDTO;
import com.biddingSystem.Entity.Product;
import com.biddingSystem.Entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.*;

@Getter
@Setter
@Component
public class EntityMapper {
    private  Map<Long,Product> productIdVsProduct;
    private  Map<Long, User> userIdVsUser;
    private Map<Long, List<BidDTO>> productBidList;
    EntityMapper(){
        productIdVsProduct = new HashMap<>();
        productBidList = new HashMap<>();
        userIdVsUser = new HashMap<>();
    }
}
