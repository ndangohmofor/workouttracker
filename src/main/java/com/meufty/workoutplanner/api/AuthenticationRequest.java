package com.meufty.workoutplanner.api;

import lombok.Data;

@Data
public class AuthenticationRequest {

    private String username;
    private String password;
}
