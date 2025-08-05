package com.socialLink.Dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileIntoDto {

    private String firstName;
    private String lastName;
    private String company;
    private String position;
    private String location;
    private String about;

}
