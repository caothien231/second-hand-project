package com.example.jwt_autho.controllers;

import com.example.jwt_autho.dtos.CreateProductDto;
import com.example.jwt_autho.entities.Product;
import com.example.jwt_autho.services.ProductService;
import com.example.jwt_autho.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private UserService userService;

    // public ProductController(ProductService productService) {
    //     this.productService = productService;
    // }

    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody CreateProductDto createProductDto) {
        Product product = productService.createProduct(createProductDto);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();

        if (products.isEmpty()) {
            // Return 204 No Content if no products are found
            return ResponseEntity.noContent().build();
        }

        // Return 200 OK with the list of products
        return ResponseEntity.ok(products);
    }

    //api call when user like a product
    @PostMapping("/{productId}/like")
    public ResponseEntity<String> likeProduct(
        @RequestParam Integer userId,
        @PathVariable Integer productId
    ) {
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

}
