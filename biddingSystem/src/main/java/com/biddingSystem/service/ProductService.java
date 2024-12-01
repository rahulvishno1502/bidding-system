package com.biddingSystem.service;

import com.biddingSystem.Enums.ProductCategory;
import com.biddingSystem.Entity.Product;
import com.biddingSystem.Enums.ProductStatus;
import org.springframework.data.domain.Page;

public interface ProductService {
    public Page<Product> getProductsByCategory(ProductCategory category, int page, int size);
    public Page<Product> getProductsByCategoryAndProductStatus(ProductCategory category, ProductStatus status, int page, int size);
}
