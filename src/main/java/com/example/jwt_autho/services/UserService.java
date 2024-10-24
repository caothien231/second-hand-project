package com.example.jwt_autho.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jwt_autho.dtos.RegisterUserDto;
import com.example.jwt_autho.entities.Product;
import com.example.jwt_autho.entities.ProductStatusEnum;
import com.example.jwt_autho.entities.Role;
import com.example.jwt_autho.entities.RoleEnum;
import com.example.jwt_autho.entities.User;
import com.example.jwt_autho.repositories.ProductRepository;
import com.example.jwt_autho.repositories.RoleRepository;
import com.example.jwt_autho.repositories.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    // private final UserRepository userRepository;

    // private final RoleRepository roleRepository;

    // private final PasswordEncoder passwordEncoder;

    // public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
    //     this.userRepository = userRepository;
    //     this.roleRepository = roleRepository;
    //     this.passwordEncoder = passwordEncoder;
    // }
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EmailService mailService;

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }

    public User createAdministrator(RegisterUserDto input) {
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.ADMIN);

        if (optionalRole.isEmpty()) {
            return null;
        }

        var user = new User();
        user.setFullName(input.getFullName());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRole(optionalRole.get());

        return userRepository.save(user);
    }

    // service for like a product
    @Transactional
    public void likeProduct(Integer userId, Integer productId) {
        // Fetch user and product from repositories
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        // Check if the user has already liked the product
        if (!user.getLikedProducts().contains(product)) {
            // Add the product to the user's liked products set
            user.getLikedProducts().add(product);
            
            // Add user to the product's likedByUsers set
            product.getLikedByUsers().add(user);
        }
        
        // Save the user to persist the change in liked products
        userRepository.save(user); // This will now only save the user's changes without triggering a StackOverflowError
    }

    // unlike a product
    @Transactional
    public void unlikeProduct(Integer userId, Integer productId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        // Check if the user has already liked the product
        if (user.getLikedProducts().contains(product)) {
            user.getLikedProducts().remove(product);
            product.getLikedByUsers().remove(user);
        }else {
            throw new IllegalArgumentException("Product not liked by user");
        }
        
        userRepository.save(user);
    }

    // get products that a user liked
    @Transactional
    public Set<Product> getLikedProducts(Integer userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getLikedProducts();
    }

    @Transactional
    public ResponseEntity<String> buyProduct(Integer userId, Integer productId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getStatus() == ProductStatusEnum.SOLD) {
            throw new IllegalArgumentException("Product is already sold");
        }

        product.setBuyer(user);
        product.setStatus(ProductStatusEnum.SOLD);

        productRepository.save(product);

        String userEmail = user.getEmail();
        String productName = product.getName(); 

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("userName", user.getFullName()); 
        templateModel.put("productName", productName);   

        // Send the confirmation email using the mailService
        try {
            mailService.sendEmail(userEmail, "Purchase Confirmation", "thank-you-email", templateModel);
        } catch (MessagingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending email");
        }

        return ResponseEntity.ok("Purchase successful, confirmation email sent.");
    }
}