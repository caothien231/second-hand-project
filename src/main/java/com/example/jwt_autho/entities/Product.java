package com.example.jwt_autho.entities;

import java.util.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name; // can be the title of post
    private double price;
    private String description;

    @Column(nullable = true) 
    private String imageUrl;

    // Product status: AVAILABLE, UNAVAILABLE, PENDING, SOLD
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatusEnum status = ProductStatusEnum.AVAILABLE; 
    //Seller association
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    // Buyer association (if the product is sold)
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer; 

    // Many-to-many relationship with users who like the product
    @ManyToMany(mappedBy = "likedProducts")
    @JsonIgnore
    private Set<User> likedByUsers = new HashSet<>();

    private boolean deleted = false; // Tracks whether the product is deleted, soft delete

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdDate; 

    @UpdateTimestamp
    private Date deletedDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return id != null && id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return 31; 
    }
}