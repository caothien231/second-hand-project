package com.example.jwt_autho.controllers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.jwt_autho.services.OpenAiService;

@RestController
@RequestMapping("/test-ai")
public class OpenAiController {
    private final OpenAiService openAiService;

    @Autowired
    public OpenAiController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping("/check-description")
    public ResponseEntity<String> checkProductDescription(@RequestBody String description) {
        boolean isLegal = openAiService.isProductDescriptionLegal(description);
        
        if (isLegal) {
            return new ResponseEntity<>("The product description is legal.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("The product description is illegal.", HttpStatus.BAD_REQUEST);
        }
    }
}