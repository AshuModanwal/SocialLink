package com.socialLink.Dtos;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequestBody {


    @NotBlank(message = "userName is required")
    private String userName;

    @Email(message = "Email is required")
    private String email;

    @NotBlank(message = "password is required")
    private String password;

    public RegisterRequestBody(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public RegisterRequestBody() {
        super();
    }
}
