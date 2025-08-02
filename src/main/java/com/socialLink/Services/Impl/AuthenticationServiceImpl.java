package com.socialLink.Services.Impl;

import com.socialLink.Dtos.LoginRequestBody;
import com.socialLink.Dtos.RegisterRequestBody;
import com.socialLink.Models.UserModel;
import com.socialLink.Repositories.UserRepository;
import com.socialLink.Services.AuthenticationService;
import com.socialLink.Utils.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    @Override
    public void register(RegisterRequestBody registerRequestBody) {

        if (userRepository.getUserByEmail(registerRequestBody.getEmail()) != null) {
            throw new IllegalArgumentException("Email already in use");
        }
        // 2) Hash & save
        UserModel user = new UserModel();
        user.setEmail(registerRequestBody.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestBody.getPassword()));
        userRepository.save(user);
    }

    @Override
    public String login(LoginRequestBody loginRequestBody) {
        UserModel user = userRepository.getUserByEmail(loginRequestBody.getEmail());
        // 3) Validate credentials
        if (user == null || !passwordEncoder.matches(loginRequestBody.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        // 4) Issue JWT
        return jwtService.generateToken(user.getEmail());
    }

    @Override
    public String sendEmailVerificationToken(String token) {
        String email = jwtService.extractEmail(token);
        if(email==null)
            throw new IllegalArgumentException("Token is invalid or expired");

        UserModel user = userRepository.getUserByEmail(email);
        if(user==null)
            throw new IllegalArgumentException("User not found");



        return "";
    }


}
