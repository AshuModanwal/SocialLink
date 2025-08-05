package com.socialLink.Controllers;

import com.socialLink.Dtos.ForgetUpdatePasswordDto;
import com.socialLink.Dtos.LoginRequestBody;
import com.socialLink.Dtos.ProfileIntoDto;
import com.socialLink.Dtos.RegisterRequestBody;
import com.socialLink.Models.UserModel;
import com.socialLink.Services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody RegisterRequestBody body) {
        System.out.println("register api is called");
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

        System.out.println("http://localhost:8080/auth/send-email-verification-token \n Api called");

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token  = authHeader.substring(7);
        }

        if(token==null)
            throw new IllegalArgumentException("Jwt token is missing");

        String verificationToken = authService.sendEmailVerificationToken(token);
        return  ResponseEntity.ok(Map.of("Status", verificationToken));
    }

    @GetMapping("/validate-email-verification-token")
    public ResponseEntity<Map<String, String>> validateEmailVerificationToken(
             @RequestParam("token") String token
    ){
        System.out.println("validate-email-verification-token api called");

        String verificationToken = authService.validateEmailVerificationToken(token);
        return  ResponseEntity.ok(Map.of("Status", verificationToken));

    }

    @PutMapping("/send-forget-password-token")
    public ResponseEntity<Map<String,String >> sendForgetPasswordToken(
            HttpServletRequest request
    ) {
        System.out.println("http://localhost:8080/auth/send-forget-password-token \n Api called");

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        if (token == null)
            throw new IllegalArgumentException("Jwt token is missing");

        String verificationToken = authService.sendForgetPasswordToken(token);
        return ResponseEntity.ok(Map.of("Status", verificationToken));

    }

    @GetMapping("/validate-forget-password-token")
    public ResponseEntity<Map<String, String>> validateForgetPasswordToken(
            @RequestParam("token") String token
    ){
        System.out.println("validate-forget-password-token api called");

        String verificationToken = authService.validateForgetPasswordToken(token);
        return  ResponseEntity.ok(Map.of("Status", verificationToken));

    }

    @PutMapping("/forget-password-update")
    public ResponseEntity<Map<String , String >> forgetPasswordUpdate(
            @RequestBody ForgetUpdatePasswordDto forgetUpdatePasswordDto
            ){

        System.out.println("forget-password-update api called");

        String verificationToken = authService.forgetPasswordUpdate(forgetUpdatePasswordDto);
        return  ResponseEntity.ok(Map.of("Status", verificationToken));

    }

    @PutMapping("/profile/{id}/info")
    public ResponseEntity<Map<String, UserModel>> addProfileInfo(
            @PathVariable int id,
            @RequestBody ProfileIntoDto profileIntoDto
            ){

        UserModel user = authService.addProfileInfo(id, profileIntoDto);
        return ResponseEntity.ok(Map.of("user", user));
    }

    @PutMapping("/profile/{id}/profile-picture")
    public ResponseEntity<Map<String, UserModel>> addProfilePicture(
            @PathVariable int id,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture
    ) throws IOException {
        UserModel user = authService.addProfilePicture(id, profilePicture);
        return ResponseEntity.ok(Map.of("user", user));
    }

    @PutMapping("/profile/{id}/cover-picture")
    public ResponseEntity<Map<String, UserModel>> addCoverPicture(
            @PathVariable int id,
            @RequestParam(value = "coverPicture", required = false) MultipartFile coverPicture
    ) throws IOException {
        UserModel user = authService.addCoverPicture(id, coverPicture);
        return ResponseEntity.ok(Map.of("user", user));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, UserModel>>  getUserById(@PathVariable int id) {
        UserModel user = authService.getUserById(id);
        return ResponseEntity.ok(Map.of("user", user));
    }

}
