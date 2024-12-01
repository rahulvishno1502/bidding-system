package com.biddingSystem;

import com.biddingSystem.Entity.Product;
import com.biddingSystem.repository.ProductRepository;
import com.biddingSystem.Enums.ProductCategory;
import com.biddingSystem.service.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductByCategory_returnRelevantProductsForGivenCategory() {
        ProductCategory targetCategory = ProductCategory.ELECTRONICS;
        Pageable pageable = PageRequest.of(0, 10);

        Product electronicsProduct1 = new Product();
        electronicsProduct1.setCategory(ProductCategory.ELECTRONICS);

        Product electronicsProduct2 = new Product();
        electronicsProduct2.setCategory(ProductCategory.ELECTRONICS);

        Product furnitureProduct = new Product();
        furnitureProduct.setCategory(ProductCategory.FURNITURE);

        Product otherProduct = new Product();
        otherProduct.setCategory(ProductCategory.OTHERS);

        List<Product> productList = List.of(electronicsProduct1, electronicsProduct2, furnitureProduct, otherProduct);

        List<Product> electronicsProducts = List.of(electronicsProduct1, electronicsProduct2);
        Page<Product> productPage = new PageImpl<>(electronicsProducts);

        when(productRepository.findByCategory(targetCategory, pageable)).thenReturn(productPage);

        Page<Product> result = productServiceImpl.getProductsByCategory(targetCategory, 0, 10);

        assertEquals(2, result.getContent().size());
        assertEquals(electronicsProduct1, result.getContent().get(0));
        assertEquals(electronicsProduct2, result.getContent().get(1));

        verify(productRepository, times(1)).findByCategory(targetCategory, pageable);
    }
}
