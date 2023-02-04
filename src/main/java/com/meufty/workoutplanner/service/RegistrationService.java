package com.meufty.workoutplanner.service;

import com.meufty.workoutplanner.api.RegistrationRequest;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.model.UserRole;
import com.meufty.workoutplanner.token.ConfirmationToken;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final MyUserDetailsService myUserDetailsService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailValidator emailValidator;

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            throw new IllegalStateException(request.getEmail() + " not a valid email");
        }
        return myUserDetailsService.signUpUser(new MyUser(
                        request.getUsername(),
                        request.getEmail(),
                        request.getEmail(),
                        UserRole.USER
                )
        );
    }

    @Transactional
    public String confirmToken(String token){
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null){
            throw new IllegalStateException("Email address already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())){
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        myUserDetailsService.enableUser(
                confirmationToken.getMyUser().getUsername()
        );
        return "confirmed";
    }
}
