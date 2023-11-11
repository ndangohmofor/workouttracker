package com.meufty.workoutplanner.service;


import com.meufty.workoutplanner.api.UserProfileRequest;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.model.MyUserDetails;
import com.meufty.workoutplanner.model.UserProfile;
import com.meufty.workoutplanner.model.UserRole;
import com.meufty.workoutplanner.repository.UserProfileRepository;
import com.meufty.workoutplanner.repository.UserRepository;
import com.meufty.workoutplanner.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    JwtUtil jwtUtil;
    UserRepository userRepository;

    public void createUserProfile(UserProfile request) {
        userProfileRepository.save(request);
    }

    @Secured("ROLE_USER")   
    @PostAuthorize("returnObject. == authentication.id")
    public ResponseEntity<?> fetchUserProfile(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization");
        if (accessToken == null){
            return ResponseEntity.status(401).body(HttpServletResponse.SC_UNAUTHORIZED);
        }
        String username = jwtUtil.extractUsername(accessToken);
        MyUser user = userRepository.findByUsername(username).get();
        UserProfile profile = userProfileRepository.findUserProfileByUserId(user.getId()).orElse(null);
        UserProfileRequest profileRequest = new UserProfileRequest();
        if (profile != null) {
            profileRequest.setFirstName(profile.getFirstName());
            profileRequest.setLastName(profile.getLastName());
            profileRequest.setPreferredName(profile.getPreferredName());
            profileRequest.setUserRole(profile.getRole());
            profileRequest.setProfilePhoto(profile.getProfilePhoto());
            profileRequest.setGoal(profile.getGoal());
        }
        return ResponseEntity.ok(profileRequest);
    }

    @Secured("{'ROLE_ADMIN', 'ROLE_EMPLOYEE'}")
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
