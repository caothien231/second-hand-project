package com.example.jwt_autho.repositories;

import com.example.jwt_autho.entities.Product;
import com.example.jwt_autho.entities.ProductStatusEnum;
import com.example.jwt_autho.entities.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByStatus(ProductStatusEnum status);

    List<Product> findBySeller(User seller);

    List<Product> findByBuyer(User buyer);
}