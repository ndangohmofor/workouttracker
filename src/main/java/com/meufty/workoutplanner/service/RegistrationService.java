package com.meufty.workoutplanner.service;

import com.meufty.workoutplanner.api.RegistrationRequest;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    public String register(RegistrationRequest request) {
        return "works";
    }
}
