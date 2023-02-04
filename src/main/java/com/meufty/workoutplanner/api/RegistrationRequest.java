package com.meufty.workoutplanner.api;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrationRequest {
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;
    private final boolean isActive = false;
    private final boolean isEnabled = true;
    private final String email;
}
