package com.meufty.workoutplanner.api;

import lombok.Data;

@Data
public class RegistrationRequest {
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;
    private final boolean isActive = false;
    private final boolean isEnabled = true;
    private final String email;
}
