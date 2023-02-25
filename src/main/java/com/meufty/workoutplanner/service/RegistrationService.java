package com.meufty.workoutplanner.service;

import com.meufty.workoutplanner.api.RegistrationRequest;
import com.meufty.workoutplanner.email.EmailSender;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.model.UserRole;
import com.meufty.workoutplanner.token.ConfirmationToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class RegistrationService {

    private final MyUserDetailsService myUserDetailsService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailValidator emailValidator;
    private final EmailSender emailSender;

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            log.error(request.getEmail() + " is not a valid email address.");
            throw new IllegalStateException(request.getEmail() + " not a valid email. Please correct the email and try again");
        }
        try {
            String token = myUserDetailsService.signUpUser(new MyUser(request.getUsername(), request.getEmail(), request.getEmail(), UserRole.USER));
            emailSender.send(request.getEmail(), request.getFirstName(), token);
            log.info("Confirmation email sent successfully to " + request.getEmail());
            return token;
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
