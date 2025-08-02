package com.socialLink.Services;


import org.springframework.stereotype.Component;

public interface JwtService {


    String generateToken(String email);
    String extractEmail(String token);
}
