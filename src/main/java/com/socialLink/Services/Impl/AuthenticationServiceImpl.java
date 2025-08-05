package com.socialLink.Services.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.socialLink.Dtos.*;
import com.socialLink.Models.UserModel;
import com.socialLink.Repositories.UserRepository;
import com.socialLink.Services.AuthenticationService;
import com.socialLink.Utils.EmailService;
import com.socialLink.Utils.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;


@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {


    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    @Autowired
    private Cloudinary cloudinary;

    @Value("${url.link}")
    private String urlLink;

    @Autowired
    EmailService emailService;
    @Override
    public void register(RegisterRequestBody registerRequestBody) {

        if (userRepository.getUserByEmail(registerRequestBody.getEmail()) != null) {
            throw new IllegalArgumentException("Email already in use");
        }
        // 2) Hash & save
        UserModel user = new UserModel();
        user.setEmail(registerRequestBody.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestBody.getPassword()));

        System.out.println("user:  "+user);
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
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + 3_600_000);
        String email = jwtService.extractEmail(token);
        if(email==null)
            throw new IllegalArgumentException("Token is invalid or expired");

        UserModel user = userRepository.getUserByEmail(email);
        if(user==null)
            throw new IllegalArgumentException("User not found");

        if(user.isEmailVerified())
            throw new IllegalArgumentException("user email already verified");

        EmailBody emailBody = new EmailBody();
        String link = urlLink + "/auth/validate-email-verification-token?token="
                + URLEncoder.encode(token, StandardCharsets.UTF_8);

        user.setEmailVerificationTokenExpiryDate(expiry);
        user.setEmailVerificationToken(token);

        userRepository.save(user);

        emailBody.setRecipient(email);
        emailBody.setMsgBody(link);
        emailBody.setSubject("Email verification");

        return emailService.sendEmail(emailBody);
    }

    @Override
    public String validateEmailVerificationToken(String token) {

        System.out.println("token: "+ token);
        String email = jwtService.extractEmail(token);
        System.out.println("email:  "+email);
        if(email==null)
            throw new IllegalArgumentException("Token is invalid or expired");

        UserModel user = userRepository.getUserByEmail(email);
        if(user==null)
            throw new IllegalArgumentException("User not found");

        if(user.isEmailVerified())
            throw new IllegalArgumentException("Email is already verified");

        if(user.getEmailVerificationTokenExpiryDate().before(new Date())){
            throw new IllegalArgumentException("Email token is expired, please send another mail");

        }

        if(!user.getEmailVerificationToken().equals(token)){
            throw new IllegalArgumentException("Email verification token is invalid");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);        // optional: clear token
        user.setEmailVerificationTokenExpiryDate(null);
        userRepository.save(user);

        return "user email verified successully";


    }


    @Override
    public String sendForgetPasswordToken(String token) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + 3_600_000);
        String email = jwtService.extractEmail(token);
        if(email==null)
            throw new IllegalArgumentException("Token is invalid or expired");

        UserModel user = userRepository.getUserByEmail(email);
        if(user==null)
            throw new IllegalArgumentException("User not found");


        EmailBody emailBody = new EmailBody();
        String link = urlLink + "/auth/validate-forget-password-token?token="
                + URLEncoder.encode(token, StandardCharsets.UTF_8);

        user.setForgetPasswordToken(token);
        user.setForgetPasswordTokenExpiryDate(expiry);

        userRepository.save(user);

        emailBody.setRecipient(email);
        emailBody.setMsgBody(link);
        emailBody.setSubject("Forget password token");

        return emailService.sendEmail(emailBody);
    }

    @Override
    public String validateForgetPasswordToken(String token) {
        System.out.println("token: "+ token);
        String email = jwtService.extractEmail(token);
        System.out.println("email:  "+email);
        if(email==null)
            throw new IllegalArgumentException("Reset Password Token is invalid or expired");

        UserModel user = userRepository.getUserByEmail(email);
        if(user==null)
            throw new IllegalArgumentException("User not found");

        if(user.getForgetPasswordTokenExpiryDate().before(new Date())){
            throw new IllegalArgumentException("Forget password token is expired, please send another mail");
        }

        if(!user.getForgetPasswordToken().equals(token)){
            throw new IllegalArgumentException("Forget password verification token is invalid");
        }

        return "user forget password token verification successfully";
    }

    @Override
    public String forgetPasswordUpdate(ForgetUpdatePasswordDto forgetUpdatePasswordDto) {
        String token = forgetUpdatePasswordDto.getToken();
        String email = jwtService.extractEmail(token);
        System.out.println("email:  "+email);
        if(email==null)
            throw new IllegalArgumentException("Reset Password Token is invalid or expired");

        UserModel user = userRepository.getUserByEmail(email);
        if(user==null)
            throw new IllegalArgumentException("User not found");

        if(user.getForgetPasswordTokenExpiryDate().before(new Date())){
            throw new IllegalArgumentException("Forget password token is expired, please send another mail");
        }

        if(!user.getForgetPasswordToken().equals(token)){
            throw new IllegalArgumentException("Forget password verification token is invalid");
        }

        // now update the user password

        user.setPassword(passwordEncoder.encode(forgetUpdatePasswordDto.getNewPassword()));

        userRepository.save(user);

        return "User password updated successfully";
    }

    @Override
    public UserModel addProfileInfo(int id, ProfileIntoDto profileIntoDto) {
        UserModel user = userRepository.getUserById(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (profileIntoDto.getFirstName() != null) {
            user.setFirstName(profileIntoDto.getFirstName());
        }
        if (profileIntoDto.getLastName() != null) {
            user.setLastName(profileIntoDto.getLastName());
        }
        if (profileIntoDto.getCompany() != null) {
            user.setCompany(profileIntoDto.getCompany());
        }
        if (profileIntoDto.getPosition() != null) {
            user.setPosition(profileIntoDto.getPosition());
        }
        if (profileIntoDto.getLocation() != null) {
            user.setLocation(profileIntoDto.getLocation());
        }
        if (profileIntoDto.getAbout() != null) {
            user.setAbout(profileIntoDto.getAbout());
        }
        // Save changes
        userRepository.save(user);
        return user;
    }

    @Override
    public UserModel addProfilePicture(int id, MultipartFile profilePicture) {
        if (profilePicture == null || profilePicture.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file uploaded.");
        }

        UserModel user = userRepository.getUserById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        File convFile = null;
        try {
            // Save file temporarily
            convFile = new File(System.getProperty("java.io.tmpdir") + "/" + profilePicture.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(convFile)) {
                fos.write(profilePicture.getBytes());
            }

            // Upload to Cloudinary
            var uploadResult = cloudinary.uploader().upload(convFile,
                    ObjectUtils.asMap("folder", "socialLink/profile-picture"));
            String imageUrl = uploadResult.get("url").toString();

            // Update user
            user.setProfilePicture(imageUrl);
            userRepository.save(user);

            return user;

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to upload the file.");
        } finally {
            if (convFile != null && convFile.exists()) {
                convFile.delete(); // Clean up temp file
            }
        }
    }

    @Override
    public UserModel addCoverPicture(int id, MultipartFile coverPicture) {


        if (coverPicture == null || coverPicture.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No file uploaded.");
        }

        UserModel user = userRepository.getUserById(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        File convFile = null;
        try {
            // Save file temporarily
            convFile = new File(System.getProperty("java.io.tmpdir") + "/" + coverPicture.getOriginalFilename());
            try (FileOutputStream fos = new FileOutputStream(convFile)) {
                fos.write(coverPicture.getBytes());
            }

            // Upload to Cloudinary
            var uploadResult = cloudinary.uploader().upload(convFile,
                    ObjectUtils.asMap("folder", "socialLink/cover-picture"));

            String imageUrl = uploadResult.get("url").toString();

            // Update user
            user.setCoverPicture(imageUrl);
            userRepository.save(user);

            return user;

        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to upload the file.");
        } finally {
            if (convFile != null && convFile.exists()) {
                convFile.delete(); // Clean up temp file
            }
        }
    }

    @Override
    public UserModel getUserById(int id) {
        UserModel user = userRepository.getUserById(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }

        return user;
    }


}
