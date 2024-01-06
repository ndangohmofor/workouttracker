package com.meufty.workoutplanner.api;

import com.meufty.workoutplanner.model.UserRole;
import lombok.Data;

@Data
public class UserProfileRequest {
    private String firstName;
    private String lastName;
    private String preferredName;
    private String middleName;
    private String username;
    private UserRole userRole;
    private String goal;
    private byte[] profilePhoto;
}
