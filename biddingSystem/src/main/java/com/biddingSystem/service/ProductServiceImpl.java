package com.biddingSystem.service;

import com.biddingSystem.Enums.ProductCategory;

import com.biddingSystem.Enums.ProductStatus;
import com.biddingSystem.Entity.Product;
import com.biddingSystem.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;

    @Autowired
    ProductServiceImpl(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    // Method to fetch products by category with pagination
    public Page<Product> getProductsByCategory(ProductCategory category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategory(category, pageable);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> findAllProduct() {
        return productRepository.findAll();
    }

    public Page<Product> getProductsByCategoryAndProductStatus(ProductCategory category, ProductStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByCategoryAndProductStatus(category, status,pageable);
    }
}
