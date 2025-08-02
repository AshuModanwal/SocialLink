package com.socialLink.Controllers;

import com.socialLink.Dtos.LoginRequestBody;
import com.socialLink.Dtos.RegisterRequestBody;
import com.socialLink.Services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody RegisterRequestBody body) {
        authService.register(body);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @Valid @RequestBody LoginRequestBody body) {
        String token = authService.login(body);
        return ResponseEntity
                .ok(Map.of("token", token));
    }

    @GetMapping("/send-email-verification-token")
    public ResponseEntity<Map<String, String>> sendEmailVerificationToken(HttpServletRequest request){

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token  = authHeader.substring(7);
        }

        if(token==null)
            throw new IllegalArgumentException("Jwt token is missing");

        String verificationToken = authService.sendEmailVerificationToken(token);
        return  ResponseEntity.ok(Map.of("verificationToken", verificationToken));

    }
}
