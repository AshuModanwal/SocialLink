package com.socialLink.Dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgetUpdatePasswordDto {

    private String token;
    private String newPassword;
}
