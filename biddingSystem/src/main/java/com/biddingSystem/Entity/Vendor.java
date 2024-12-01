package com.biddingSystem.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Vendor")
public class Vendor implements PersonEntity{
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email_id")
    private String emailId;

    @OneToMany
    @JoinColumn(name = "vendor_id")
    private List<Product> productsForSelling;

    @Override
    public String getName() {
        return this.firstName+" "+this.lastName;
    }

    @Override
    public String getEmail() {
        return this.emailId;
    }
}
