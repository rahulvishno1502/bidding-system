package com.biddingSystem.service;

import com.biddingSystem.Entity.Product;
import com.biddingSystem.Entity.Vendor;

public interface VendorService {
    public Vendor addProductToVendor(Long vendorId, Product product);
    public Vendor saveVendor(Vendor vendor);
}
