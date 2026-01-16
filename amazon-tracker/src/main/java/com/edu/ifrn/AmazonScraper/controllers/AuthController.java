package com.edu.ifrn.AmazonScraper.controllers;

import com.edu.ifrn.AmazonScraper.dtos.TokenDTO;
import com.edu.ifrn.AmazonScraper.entities.User;
import com.edu.ifrn.AmazonScraper.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<TokenDTO> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody User user) {
        return ResponseEntity.ok(userService.login(user));
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, world!";
    }
}
