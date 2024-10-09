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
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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

    // upload image from local
    // using firebase to store product images for now (so comment out this api)
    // @PostMapping("/upload-image")
    // public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
    //     // Validate the file (optional)
    //     if (file.isEmpty()) {
    //         return ResponseEntity.badRequest().body("Please select a file to upload.");
    //     }

    //     try {
    //         // Create the uploads directory if it doesn't exist
    //         File uploadsDir = new File("uploads");
    //         if (!uploadsDir.exists()) {
    //             uploadsDir.mkdir();
    //         }

    //         // Define the path for the uploaded file
    //         String filePath = "uploads/" + file.getOriginalFilename();
    //         File destFile = new File(filePath);
            
    //         // Save the file to the local filesystem
    //         file.transferTo(destFile);
            
    //         // Return the file path as the response
    //         return ResponseEntity.ok(filePath);
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body("Failed to upload the image: " + e.getMessage());
    //     }
    // }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Integer productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.ok("Product deleted successfully");
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

    @GetMapping("/get/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer productId) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

}
