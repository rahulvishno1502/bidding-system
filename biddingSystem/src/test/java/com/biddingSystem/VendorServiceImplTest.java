package com.biddingSystem;

import com.biddingSystem.mapper.EntityMapper;
import com.biddingSystem.Entity.Product;
import com.biddingSystem.Entity.Vendor;
import com.biddingSystem.repository.ProductRepository;
import com.biddingSystem.repository.VendorRepository;
import com.biddingSystem.service.VendorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class VendorServiceImplTest {
    @Mock
    private VendorRepository vendorRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private EntityMapper entityMapper;
    @InjectMocks
    private VendorServiceImpl vendorServiceImpl;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addProductToVendor_shouldSaveProductSuccessfully(){
        Long vendorId = 1L;
        Product product = new Product();
        Vendor vendor = new Vendor();
        vendor.setProductsForSelling(new ArrayList<>());

        Map<Long,Product> mockProductMap = mock(Map.class);

        when(vendorRepository.findById(vendorId)).thenReturn(Optional.of(vendor));
        when(productRepository.save(product)).thenReturn(product);
        when(vendorRepository.save(vendor)).thenReturn(vendor);
        when(entityMapper.getProductIdVsProduct()).thenReturn(mockProductMap);

        Vendor result = vendorServiceImpl.addProductToVendor(vendorId, product);

        assertEquals(vendor, result);
        verify(productRepository, times(1)).save(product);
        verify(mockProductMap, times(1)).putIfAbsent(product.getId(), product);
    }

    @Test
    void addProductToVendor_throwExceptionWhenVendorNotFound(){
        Long vendorId = 1L;
        Product product = new Product();
        when(vendorRepository.findById(vendorId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> vendorServiceImpl.addProductToVendor(vendorId, product));
    }
}
