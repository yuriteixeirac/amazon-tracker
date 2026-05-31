package com.edu.ifrn.AmazonScraper.services;

import com.edu.ifrn.AmazonScraper.dtos.LoginRequestDTO;
import com.edu.ifrn.AmazonScraper.dtos.RegisterRequestDTO;
import com.edu.ifrn.AmazonScraper.dtos.TokenDTO;
import com.edu.ifrn.AmazonScraper.entities.User;
import com.edu.ifrn.AmazonScraper.exceptions.EntityAlreadyExistsException;
import com.edu.ifrn.AmazonScraper.exceptions.EntityNotFoundException;
import com.edu.ifrn.AmazonScraper.repositories.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final PasswordEncoder encoder;

    public UserService(
            UserRepository userRepository,
            JWTService jwtService,
            PasswordEncoder encoder
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.encoder = encoder;
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

    public TokenDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EntityAlreadyExistsException("Email is already registered.");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));
        userRepository.save(user);

        return new TokenDTO(jwtService.generateToken(user.getEmail()));
    }

    public TokenDTO login(LoginRequestDTO request) {
        User existingUser = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials."));

        if (!encoder.matches(request.password(), existingUser.getPassword())) {
            throw new BadCredentialsException("Invalid credentials.");
        }

        return new TokenDTO(jwtService.generateToken(existingUser.getEmail()));
    }
}
