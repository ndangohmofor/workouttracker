package com.meufty.workoutplanner.service;


import com.meufty.workoutplanner.api.UserProfileRequest;
import com.meufty.workoutplanner.model.UserProfile;
import com.meufty.workoutplanner.repository.UserProfileRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserProfileService {
    private UserProfileRepository userProfileRepository;

    public void createUserProfile(UserProfile request){
        userProfileRepository.save(request);
    }

    public UserProfileRequest fetchUserProfile(Long userId){
        UserProfile profile = userProfileRepository.findUserProfileByUserId(userId).orElse(null);
        UserProfileRequest profileRequest = null;
        if (profile != null){
            profileRequest.setFirstName(profile.getFirstName());
            profileRequest.setLastName(profile.getLastName());
            profileRequest.setPreferredName(profile.getPreferredName());
            profileRequest.setUserRole(profile.getRole());
            profileRequest.setProfilePhoto(profile.getProfilePhoto());
            profileRequest.setGoal(profile.getGoal());
        }
        return profileRequest;
    }
}
