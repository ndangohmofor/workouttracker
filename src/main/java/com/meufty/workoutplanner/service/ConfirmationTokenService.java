package com.meufty.workoutplanner.service;

import com.meufty.workoutplanner.repository.ConfirmationTokenRepository;
import com.meufty.workoutplanner.token.ConfirmationToken;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepo;

    public void saveConfirmationToken(ConfirmationToken token){
        confirmationTokenRepo.save(token);
    }
}
