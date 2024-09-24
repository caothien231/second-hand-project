package com.example.jwt_autho.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwt_autho.entities.Product;
import com.example.jwt_autho.entities.User;
import com.example.jwt_autho.services.ProductService;
import com.example.jwt_autho.services.UserService;

import java.util.*;

@RequestMapping("/api/users")
@RestController
public class UserController {

    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    // public UserController(UserService userService) {
    //     this.userService = userService;
    // }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()") // if authenticated, can go this
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")//only admin and super admin
    public ResponseEntity<List<User>> allUsers() {
        List <User> users = userService.allUsers();

        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/{userId}/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER')")
    public ResponseEntity<List<Product>> getUserProducts(@PathVariable Integer userId){
        List<Product> products = productService.getProductsBySeller(userId);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content if no products found
        }

        return ResponseEntity.ok(products);
    }

    //api call when user like a product
    @PostMapping("/{userId}/like/{productId}")
    public ResponseEntity<String> likeProduct(@PathVariable Integer userId, @PathVariable Integer productId) {
        System.out.println("userId" + userId);
        System.out.println("productId" + productId);
        try {
            System.out.println("Beginningggggggggggg");
            userService.likeProduct(userId, productId);
            System.out.println("Afterrrrrrrrr");
            return ResponseEntity.ok("Product liked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // unlike a product
    @DeleteMapping("/{userId}/unlike-product/{productId}")
    public ResponseEntity<String> unlikeProduct(@PathVariable Integer userId, @PathVariable Integer productId) {
        userService.unlikeProduct(userId, productId);
        return ResponseEntity.ok("Product unliked successfully");
    }

    @GetMapping("/{userId}/liked-products")
    public ResponseEntity<Set<Product>> getLikedProducts(@PathVariable Integer userId) {
        Set<Product> likedProducts = userService.getLikedProducts(userId);
        return ResponseEntity.ok(likedProducts);
    }
}