package com.edu.ifrn.AmazonScraper.controllers;

import com.edu.ifrn.AmazonScraper.dtos.ResponseDTO;
import com.edu.ifrn.AmazonScraper.dtos.UrlDTO;
import com.edu.ifrn.AmazonScraper.entities.Product;
import com.edu.ifrn.AmazonScraper.entities.User;
import com.edu.ifrn.AmazonScraper.services.ProductService;
import com.edu.ifrn.AmazonScraper.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final UserService userService;

    public ProductController(
            ProductService productService,
            UserService userService
    ) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<Product>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> addProduct(@RequestBody UrlDTO url, Principal principal) {
        User user = userService.findByEmail(principal.getName());

        url.setUserId(user.getId());
        productService.save(user, url);

        return ResponseEntity.ok(new ResponseDTO("Product registered successfully."));
    }

    @GetMapping("/me")
    public ResponseEntity<List<Product>> meTrackingProducts(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ResponseEntity.ok(productService.findByTrackingUser(user));
    }
}
