package com.example.jwt_autho.services;

import com.example.jwt_autho.dtos.CreateProductDto;
import com.example.jwt_autho.entities.Product;
import com.example.jwt_autho.entities.ProductStatusEnum;
import com.example.jwt_autho.entities.User;
import com.example.jwt_autho.repositories.ProductRepository;
import com.example.jwt_autho.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProductService {

    //////Manually dependency injection using contructor////////// 
    // private final ProductRepository productRepository;
    // private final UserRepository userRepository;

    // public ProductService(ProductRepository productRepository, UserRepository userRepository) {
    //     this.productRepository = productRepository;
    //     this.userRepository = userRepository;
    // }

    /////////dependency injection using Spring Autowire ////////////
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    // Create new product
    @Transactional
    public Product createProduct(CreateProductDto createProductDto) {
        Optional<User> seller = userRepository.findById(createProductDto.getSellerId());
        if (seller.isEmpty()) {
            throw new IllegalArgumentException("Seller not found");
        }

        Product product = new Product();
        product.setName(createProductDto.getName());
        product.setPrice(createProductDto.getPrice());
        product.setDescription(createProductDto.getDescription());
        product.setStatus(ProductStatusEnum.valueOf(createProductDto.getStatus())); // Handle the enum conversion
        product.setSeller(seller.get());

        return productRepository.save(product);
    }

    // Get all the products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Get products from a seller
    @Transactional
    public List<Product> getProductsBySeller(Integer sellerId) {
        User seller = userRepository.findById(sellerId)
            .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        
        return productRepository.findBySeller(seller);
    }
}