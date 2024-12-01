package com.biddingSystem.service;

import com.biddingSystem.mapper.EntityMapper;
import com.biddingSystem.Entity.User;
import com.biddingSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);


    @Autowired
    UserServiceImpl(UserRepository userRepository, EntityMapper mapper){
        this.userRepository = userRepository;
        this.entityMapper = mapper;
    }

    public  User saveUser(User user) {
        User savedUser = userRepository.save(user);
        if(!entityMapper.getUserIdVsUser().containsKey(savedUser.getId())){
            entityMapper.getUserIdVsUser().put(savedUser.getId(), savedUser);
        }
        logger.info("User with Name - {} is saved Successfully", savedUser.getFirstName());
        return savedUser;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
