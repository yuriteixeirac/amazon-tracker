package com.edu.ifrn.AmazonScraper.controllers;

import com.edu.ifrn.AmazonScraper.dtos.UserDTO;
import com.edu.ifrn.AmazonScraper.entities.User;
import com.edu.ifrn.AmazonScraper.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(Principal principal) {
        return ResponseEntity.ok(UserDTO.from(userService.findByEmail(principal.getName())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id, Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        if (!currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You cannot access another user.");
        }

        return ResponseEntity.ok(UserDTO.from(currentUser));
    }
}
