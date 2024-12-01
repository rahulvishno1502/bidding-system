package com.biddingSystem.service;

import com.biddingSystem.mapper.EntityMapper;
import com.biddingSystem.Entity.Product;
import com.biddingSystem.Entity.Vendor;
import com.biddingSystem.repository.ProductRepository;
import com.biddingSystem.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class VendorServiceImpl implements VendorService{
    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository;
    private final EntityMapper entityMapper;

    Logger logger = LoggerFactory.getLogger(VendorServiceImpl.class);

    @Autowired
    VendorServiceImpl(VendorRepository vendorRepository, ProductRepository productRepository, EntityMapper mapper){
        this.entityMapper = mapper;
        this.productRepository = productRepository;
        this.vendorRepository = vendorRepository;
    }

    public Vendor addProductToVendor(Long vendorId, Product product) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor Not Found with id: " + vendorId));
        List<Product> list = vendor.getProductsForSelling();
        list.add(product);
        Product savedProduct = null;
        try {
            savedProduct = productRepository.save(product);
        } catch (Exception e) {
            logger.error("Error occurred while saving product: " + e.getMessage());
            throw new RuntimeException("Failed to save product", e);
        }

        entityMapper.getProductIdVsProduct().putIfAbsent(savedProduct.getId(), savedProduct);

        logger.info("Product saved Successfully.");
        return vendorRepository.save(vendor);
    }

    public List<Vendor> getAllVendors(){
        return vendorRepository.findAll();
    }

    public Vendor saveVendor(Vendor vendor) {
        return  vendorRepository.save(vendor);
    }
}
