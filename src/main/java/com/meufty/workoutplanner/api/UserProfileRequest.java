package com.meufty.workoutplanner.api;

import com.meufty.workoutplanner.model.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserProfileRequest {
    private String firstName;
    private String lastName;
    private String preferredName;
    private String username;
    private UserRole userRole;
    private String goal;
    private byte[] profilePhoto;
}
