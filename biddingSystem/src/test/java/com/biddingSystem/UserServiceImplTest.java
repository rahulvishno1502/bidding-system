package com.biddingSystem;

import com.biddingSystem.mapper.EntityMapper;
import com.biddingSystem.Entity.User;
import com.biddingSystem.repository.UserRepository;
import com.biddingSystem.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private EntityMapper entityMapper;
    @Mock
    private Logger logger;
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveUserTest(){
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");

        Map<Long, User> mockUserMap = mock(Map.class);
        when(userRepository.save(user)).thenReturn(user);
        when(entityMapper.getUserIdVsUser()).thenReturn(mockUserMap);

        User saveduser = userServiceImpl.saveUser(user);

        assertEquals(user, saveduser);
        verify(userRepository, times(1)).save(user);
    }
}
