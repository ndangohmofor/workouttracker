package com.meufty.workoutplanner.service;

import com.meufty.workoutplanner.repository.ConfirmationTokenRepository;
import com.meufty.workoutplanner.token.ConfirmationToken;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepo;

    public void saveConfirmationToken(ConfirmationToken token){
        confirmationTokenRepo.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token){
        return confirmationTokenRepo.findByToken(token);
    }

    public int setConfirmedAt(String token){
        return (confirmationTokenRepo.updateConfirmedAt(
                token, LocalDateTime.now()
        ));
    }
}
