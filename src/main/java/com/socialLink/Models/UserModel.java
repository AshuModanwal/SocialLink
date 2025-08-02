package com.socialLink.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")                  // ← real table name
@Data
public class UserModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull @Email
    @Column(unique = true, name = "email")
    private String email;

    @Column(name = "emailVerified")
    private boolean emailVerified = false;

    @Column(name = "emailVerificationToken")
    private String emailVerificationToken;

    @Column(name = "emailVerificationTokenExpiryDate")
    private LocalDateTime emailVerificationTokenExpiryDate;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "passwordResetToken")
    private String passwordResetToken;

    @Column(name = "passwordResetTokenExpiryDate")
    private LocalDateTime passwordResetTokenExpiryDate;

    // override default naming for these too
    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "company")
    private String company;

    @Column(name = "position")
    private String position;

    @Column(name = "location")
    private String location;

    @Column(name = "profilePicture")
    private String profilePicture;

    @Column(name = "coverPicture")
    private String coverPicture;

    @Column(name = "profileComplete")
    private boolean profileComplete=false;

    @Column(name = "about")
    private String about;


    @Override
    public String toString() {
        return "UserModel{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
