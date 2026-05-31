package com.edu.ifrn.AmazonScraper.dtos;

import com.edu.ifrn.AmazonScraper.entities.User;

public record UserDTO(Long id, String email) {
    public static UserDTO from(User user) {
        return new UserDTO(user.getId(), user.getEmail());
    }
}
