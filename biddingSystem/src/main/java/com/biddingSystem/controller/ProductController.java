package com.biddingSystem.controller;

import com.biddingSystem.Enums.ProductCategory;
import com.biddingSystem.Enums.ProductStatus;
import com.biddingSystem.Utils.ApiResponse;
import com.biddingSystem.Entity.Product;
import com.biddingSystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    private final ProductService productService;

    @Autowired
    ProductController(ProductService productService){
        this.productService = productService;
    }

    /**
     * This api helps in getting product by category and Status.
     * @param category
     * @param page
     * @param size
     * @param status
     * @return
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse> getProductsByCategoryAndStatus(
            @PathVariable("category") ProductCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam("status") ProductStatus status) {
        try {
            Page<Product> products = productService.getProductsByCategoryAndProductStatus(category,status, page, size);
            ApiResponse response = new ApiResponse(true, "Products fetched successfully", products);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            ApiResponse response = new ApiResponse(false, "Invalid category: " + e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, "Failed to fetch products", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
