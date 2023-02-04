package com.meufty.workoutplanner.service;

import com.meufty.workoutplanner.api.RegistrationRequest;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.model.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final MyUserDetailsService myUserDetailsService;
    private final EmailValidator emailValidator;

    public String register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail) {
            throw new IllegalStateException(request.getEmail() + " not a valid email");
        }
        return myUserDetailsService.signUpUser(new MyUser(
                        request.getUsername(),
                        request.getPassword(),
                        request.getEmail(),
                UserRole.USER
                )
        );
    }
}
