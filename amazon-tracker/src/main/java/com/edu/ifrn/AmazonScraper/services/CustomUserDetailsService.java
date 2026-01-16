package com.edu.ifrn.AmazonScraper.services;

import com.edu.ifrn.AmazonScraper.entities.UserPrincipal;
import com.edu.ifrn.AmazonScraper.repositories.UserRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserPrincipal(userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found")));
    }
}
