package com.meufty.workoutplanner.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthenticationResponse {
    @JsonProperty("access_token")
    private String jwtLoginToken;
    @JsonProperty("refresh_token")
    private String jwtRefreshToken;
    private String username;
    private UserRole role;
}
