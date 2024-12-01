package com.biddingSystem.repository;

import com.biddingSystem.Enums.ProductCategory;
import com.biddingSystem.Enums.ProductStatus;
import com.biddingSystem.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategory(ProductCategory category, Pageable pageable);
    Page<Product> findByCategoryAndProductStatus(ProductCategory category, ProductStatus status, Pageable pageable);
    List<Product> findByProductStatus(ProductStatus status);
}
