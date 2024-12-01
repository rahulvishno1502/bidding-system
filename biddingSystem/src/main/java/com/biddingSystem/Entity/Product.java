package com.biddingSystem.Entity;

import com.biddingSystem.Enums.ProductCategory;
import com.biddingSystem.Enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Product")
public class Product {

    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name", nullable = false)
    private String name;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private ProductCategory category = ProductCategory.OTHERS;

    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;

    @Column(name = "slot_start", nullable = false)
    private LocalDateTime slotStartTime;

    @Column(name = "slot_end", nullable = false)
    private LocalDateTime slotEndTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus = ProductStatus.AVAILABLE;

    @Column(name = "owner_id", nullable = true)
    private Long ownerId;

}
