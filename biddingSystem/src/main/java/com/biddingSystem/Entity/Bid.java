package com.biddingSystem.Entity;

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
@Table(name = "Bid")
public class Bid {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many bids can be placed by a single user, so use ManyToOne
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Many bids can be placed on a single product, so use ManyToOne
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;
}
