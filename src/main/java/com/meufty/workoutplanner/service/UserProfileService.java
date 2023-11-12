package com.meufty.workoutplanner.service;


import com.meufty.workoutplanner.api.UserProfileRequest;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.model.UserProfile;
import com.meufty.workoutplanner.model.UserRole;
import com.meufty.workoutplanner.repository.UserProfileRepository;
import com.meufty.workoutplanner.repository.UserRepository;
import com.meufty.workoutplanner.util.ExtractUserFromToken;
import com.meufty.workoutplanner.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
    ExtractUserFromToken extractUserFromToken;

    public UserProfile createUserProfile(UserProfile request) {
        return userProfileRepository.save(request);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_USER"})
    public ResponseEntity<?> fetchUserProfile(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7);
        if (accessToken == null){
            return ResponseEntity.status(401).body(HttpServletResponse.SC_UNAUTHORIZED);
        }
        String username = jwtUtil.extractUsername(accessToken);
        MyUser user = userRepository.findByUsername(username).get();
        return getUserProfileByUser(user);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    public ResponseEntity<?> fetchUserProfile(Long userId) {
        MyUser user = userRepository.findById(userId).orElseThrow();
        return ResponseEntity.ok(getUserProfileByUser(user));
    }

    private ResponseEntity<?> getUserProfileByUser(MyUser user) {
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

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    public ResponseEntity<List<UserProfileRequest>> fetchUserProfileByRole(UserRole role) {
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
        return ResponseEntity.ok(userProfiles);
    }

    //TODO method to create one's own profile
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_EMPLOYEE"})
    public UserProfile adduserProfile(HttpServletRequest httpServletRequest, UserProfileRequest request){

        MyUser user = extractUserFromToken.extractUserFromToken(httpServletRequest).getBody();

        UserProfile profile = new UserProfile();
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPreferredName(request.getPreferredName());
        profile.setGoal(request.getGoal());
        assert user != null;
        profile.setUserId(user.getId());
        profile.setProfilePhoto(request.getProfilePhoto());

        return createUserProfile(profile);
    }

    //TODO method to update one's own profile

    //TODO method to create another user's profile

    //TODO method to update another user's profile
}
