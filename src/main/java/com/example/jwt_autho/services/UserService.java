package com.example.jwt_autho.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jwt_autho.dtos.RegisterUserDto;
import com.example.jwt_autho.entities.Product;
import com.example.jwt_autho.entities.Role;
import com.example.jwt_autho.entities.RoleEnum;
import com.example.jwt_autho.entities.User;
import com.example.jwt_autho.repositories.ProductRepository;
import com.example.jwt_autho.repositories.RoleRepository;
import com.example.jwt_autho.repositories.UserRepository;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
}