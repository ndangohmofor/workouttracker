package com.meufty.workoutplanner.service;

import com.meufty.workoutplanner.api.RegistrationRequest;
import com.meufty.workoutplanner.api.UserProfileRequest;
import com.meufty.workoutplanner.email.EmailSender;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.model.UserProfile;
import com.meufty.workoutplanner.model.UserRole;
import com.meufty.workoutplanner.token.ConfirmationToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class RegistrationService {

    MyUserDetailsService myUserDetailsService;
    ConfirmationTokenService confirmationTokenService;
    EmailValidator emailValidator;
    EmailSender emailSender;
    UserProfileService userProfileService;

    private static final byte[] defaultProfilePhoto;

    static {
        try {
            defaultProfilePhoto = Files.readAllBytes(Paths.get("src/main/resources/static/images/default-profile-photo.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            log.error(request.getEmail() + " is not a valid email address.");
            throw new IllegalStateException(request.getEmail() + " not a valid email. Please correct the email and try again");
        }
        try {
            String token = myUserDetailsService.signUpUser(new MyUser(request.getUsername(), request.getEmail(), request.getPassword(), UserRole.ROLE_USER));
            emailSender.send(request.getEmail(), request.getFirstName(), token);
            log.info("Confirmation email sent successfully to " + request.getEmail());
            //TODO: As part of the registration process, user the information from registration request to create a user profile
            UserProfile profileRequest = new UserProfile();
            profileRequest.setFirstName(request.getFirstName());
            profileRequest.setLastName(request.getLastName());
            profileRequest.setUsername(request.getUsername());
            profileRequest.setRole(UserRole.ROLE_USER);
            profileRequest.setUserId(myUserDetailsService.getUserId(request.getUsername()));
            //Create a default profile picture to use for the user
            profileRequest.setProfilePhoto(defaultProfilePhoto);
            profileRequest.setGoal("");
            userProfileService.createUserProfile(profileRequest);
            return "Thanks for completing your registration. Please check your email and activate your account";
        } catch (IllegalStateException e){
            return e.getMessage();
        }
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token).orElseThrow(() -> new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            log.error("Email address already confirmed");
            throw new IllegalStateException("Email address already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            log.error("Token" + token + " expired.");
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        myUserDetailsService.enableUser(confirmationToken.getMyUser().getUsername());
        log.info("Token: " + token + " confirmed successfully");
        return "Congratulations! Your email address is confirmed. \nPlease proceed to update your profile.";
    }
}
