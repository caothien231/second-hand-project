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
import jakarta.mail.MessagingException;

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
    @Autowired
    private EmailService mailService;
    @Autowired
    private OpenAiService openAiService;

    // Create new product
    @Transactional
    public Product createProduct(CreateProductDto createProductDto) {
        Optional<User> seller = userRepository.findById(createProductDto.getSellerId());
        if (seller.isEmpty()) {
            throw new IllegalArgumentException("Seller not found");
        }

        boolean isLegal = openAiService.isProductDescriptionLegal(createProductDto.getDescription());

        if (!isLegal) {
            // Send an email notification to the seller if the product is flagged as illegal
            String sellerEmail = seller.get().getEmail();
            String productName = createProductDto.getName();
    
            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("userName", seller.get().getFullName());
            templateModel.put("productName", productName);   
    
            try {
                mailService.sendEmail(sellerEmail, "Illegal Product Alert", "illegal-product", templateModel);
            } catch (MessagingException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Error sending email regarding illegal product.");
            }
            throw new IllegalArgumentException("Product description is flagged as illegal.");
        }
        Product product = new Product();
        product.setName(createProductDto.getName());
        product.setPrice(createProductDto.getPrice());
        product.setDescription(createProductDto.getDescription());
        product.setStatus(ProductStatusEnum.valueOf(createProductDto.getStatus()));
        product.setSeller(seller.get());
        product.setDeleted(false);
        product.setImageUrl(createProductDto.getImageUrl());

        return productRepository.save(product);
    }

    // delete a product (not physically delete the product)
    @Transactional
    public void deleteProductById(Integer productId) {
        // Fetch the product by its ID
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setDeleted(true);
        product.setDeletedDate(new Date());
        product.setStatus(ProductStatusEnum.UNAVAILABLE); // Handle the enum conversion

        productRepository.save(product);
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

    public Product getProductById(Integer productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return product;
    }
}