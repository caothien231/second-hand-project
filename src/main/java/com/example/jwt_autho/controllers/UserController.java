package com.example.jwt_autho.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jwt_autho.entities.Product;
import com.example.jwt_autho.entities.User;
import com.example.jwt_autho.services.ProductService;
import com.example.jwt_autho.services.UserService;

import java.util.List;

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
}