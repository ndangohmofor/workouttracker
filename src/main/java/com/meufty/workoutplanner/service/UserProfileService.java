package com.meufty.workoutplanner.service;


import com.meufty.workoutplanner.api.UserProfileRequest;
import com.meufty.workoutplanner.model.UserProfile;
import com.meufty.workoutplanner.model.UserRole;
import com.meufty.workoutplanner.repository.UserProfileRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserProfileService {
    private UserProfileRepository userProfileRepository;

    public void createUserProfile(UserProfile request) {
        userProfileRepository.save(request);
    }

    public UserProfileRequest fetchUserProfile(Long userId) {
        UserProfile profile = userProfileRepository.findUserProfileByUserId(userId).orElse(null);
        UserProfileRequest profileRequest = new UserProfileRequest();
        if (profile != null) {
            profileRequest.setFirstName(profile.getFirstName());
            profileRequest.setLastName(profile.getLastName());
            profileRequest.setPreferredName(profile.getPreferredName());
            profileRequest.setUserRole(profile.getRole());
            profileRequest.setProfilePhoto(profile.getProfilePhoto());
            profileRequest.setGoal(profile.getGoal());
        }
        return profileRequest;
    }

    public List<UserProfileRequest> fetchUserProfileByRole(UserRole role) {
        List<UserProfile> profiles = userProfileRepository.findUserProfileByUserRole(role).orElse(new ArrayList<>());
        List<UserProfileRequest> userProfiles = new ArrayList<>();
        if (!profiles.isEmpty()) {
            profiles.forEach(p -> {
                UserProfileRequest userProfile = new UserProfileRequest();
                userProfile.setFirstName(p.getFirstName());
                userProfile.setLastName(p.getLastName());
                userProfile.setPreferredName(p.getPreferredName());
                userProfile.setGoal(p.getGoal());
                userProfile.setProfilePhoto(p.getProfilePhoto());
                userProfile.setUserRole(p.getRole());
                userProfiles.add(userProfile);
            });
        }
        ;
        return userProfiles;
    }
}
