package com.edu.ifrn.AmazonScraper.services;

import com.edu.ifrn.AmazonScraper.dtos.TokenDTO;
import com.edu.ifrn.AmazonScraper.entities.User;
import com.edu.ifrn.AmazonScraper.exceptions.EntityNotFoundException;
import com.edu.ifrn.AmazonScraper.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserService(UserRepository userRepository, JWTService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User does not exist"));
    }

    public TokenDTO register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return null; // THROW EXCEPTION
        }

        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);

        return new TokenDTO(jwtService.generateToken(user.getEmail()));
    }

    public TokenDTO login(User user) {
        User existingUser = userRepository.findByEmail(user.getEmail()).orElse(null);
        if (existingUser == null) {
            return null;
        }

        if (!encoder.matches(user.getPassword(), existingUser.getPassword())) {
            return null;
        }

        return new TokenDTO(jwtService.generateToken(user.getEmail()));
    }
}
