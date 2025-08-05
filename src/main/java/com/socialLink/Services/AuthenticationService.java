package com.socialLink.Services;

import com.socialLink.Dtos.ForgetUpdatePasswordDto;
import com.socialLink.Dtos.LoginRequestBody;
import com.socialLink.Dtos.ProfileIntoDto;
import com.socialLink.Dtos.RegisterRequestBody;
import com.socialLink.Models.UserModel;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


public interface AuthenticationService {
    void register(RegisterRequestBody registerRequestBody);

    String login(LoginRequestBody loginRequestBody);

    String sendEmailVerificationToken(String token);


    String validateEmailVerificationToken(String token );

    String sendForgetPasswordToken(String token);

    String validateForgetPasswordToken(String token);

    String forgetPasswordUpdate(ForgetUpdatePasswordDto forgetUpdatePasswordDto);

    UserModel addProfileInfo(int id, ProfileIntoDto profileIntoDto);

    UserModel addProfilePicture(int id, MultipartFile profilePicture);

    UserModel addCoverPicture(int id, MultipartFile coverPicture);

    UserModel getUserById(int id);
}
