package com.socialLink.Services;

import com.socialLink.Dtos.LoginRequestBody;
import com.socialLink.Dtos.RegisterRequestBody;
import org.springframework.stereotype.Component;


public interface AuthenticationService {
    void register(RegisterRequestBody registerRequestBody);

    String login(LoginRequestBody loginRequestBody);

    String sendEmailVerificationToken(String token);


}
