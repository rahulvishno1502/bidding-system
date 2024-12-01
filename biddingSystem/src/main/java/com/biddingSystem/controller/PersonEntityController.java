package com.biddingSystem.controller;

import com.biddingSystem.Utils.ApiResponse;
import com.biddingSystem.Utils.CommonUtils;
import com.biddingSystem.Entity.Product;
import com.biddingSystem.Entity.User;
import com.biddingSystem.Entity.Vendor;
import com.biddingSystem.service.UserService;
import com.biddingSystem.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import java.math.BigDecimal;

@RestController
public class PersonEntityController {

    private final UserService userService;
    private final VendorService vendorService;

    @Autowired
    PersonEntityController(UserService userService, VendorService vendorService){
        this.userService = userService;
        this.vendorService = vendorService;
    }

    /**
     * It registers the user in the system.
     * @param user
     * @return User
     */
    @PostMapping("/registerUser")
    public ResponseEntity<ApiResponse> createUser(@RequestBody User user){
        try {
            User savedUser = userService.saveUser(user);
            ApiResponse response = new ApiResponse(true, "User registered successfully", savedUser);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, "Failed to register user", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * It registers the vendor in the system.
     * @param vendor
     * @return Vendor
     */
    @PostMapping("/registerVendor")
    public ResponseEntity<ApiResponse> createUser(@RequestBody Vendor vendor){
        try {
            Vendor savedVendor = vendorService.saveVendor(vendor);
            ApiResponse response = new ApiResponse(true, "Vendor registered successfully", savedVendor);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse(false, "Failed to register vendor", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * The vendor need to add the product details using this api.
     * It returns various validations messages if the bid price is negative or the slot time is not right.
     * @param vendorId
     * @param product
     * @return
     */
    @PostMapping("/{vendorId}/products")
    public ResponseEntity<ApiResponse> addProductToVendor(@PathVariable Long vendorId, @RequestBody Product product) {
        if(CommonUtils.isNotValidTimeSlot(product)){
            ApiResponse response = new ApiResponse(false, "Failed to register product. Invalid Time slots", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(product.getBasePrice().compareTo(BigDecimal.ZERO) < 0){
            ApiResponse response = new ApiResponse(false, "Failed to register product. Base price cannot be negative", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try{
            Vendor updatedVendor = vendorService.addProductToVendor(vendorId, product);
            ApiResponse response = new ApiResponse(true, "Product registered successfully", updatedVendor);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }catch (Exception e){
            ApiResponse response = new ApiResponse(false, "Failed to register product", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
