package com.meufty.workoutplanner.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.meufty.workoutplanner.api.UserProfileRequest;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.model.UserProfile;
import com.meufty.workoutplanner.model.UserRole;
import com.meufty.workoutplanner.repository.UserProfileRepository;
import com.meufty.workoutplanner.repository.UserRepository;
import com.meufty.workoutplanner.util.ExtractUserFromToken;
import com.meufty.workoutplanner.util.JwtUtil;
import com.meufty.workoutplanner.util.UserProfileDeserializer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
    ObjectMapper objectMapper;

    public UserProfile createUserProfile(UserProfile request) {
        return userProfileRepository.save(request);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_USER"})
    public UserProfile fetchUserProfile(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7);
        String username = jwtUtil.extractUsername(accessToken);
        MyUser user = userRepository.findByUsername(username).orElseThrow();
        return getUserProfileByUser(user);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_USER"})
    public UserProfile fetchUserProfile(Long userId) {
        return userProfileRepository.findUserProfileByUserId(userId).orElseThrow();
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_USER"})
    public UserProfile fetchUserProfile(String username) {
        return userProfileRepository.findUserProfileByUsername(username).orElseThrow();
    }

    private UserProfile getUserProfileByUser(MyUser user) {
        UserProfile profile = userProfileRepository.findUserProfileByUserId(user.getId()).orElse(null);
        return (profile);
    }

    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    public List<UserProfileRequest> fetchUserProfileByRole(UserRole role) {
        List<UserProfile> profiles = userProfileRepository.findUserProfileByUserRole(role).orElse(new ArrayList<>());
        List<UserProfileRequest> userProfiles = new ArrayList<>();
        if (!profiles.isEmpty()) {
            profiles.forEach(p -> {
                UserProfileRequest userProfile = new UserProfileRequest();
                userProfile.setFirstName(p.getFirstName());
                userProfile.setLastName(p.getLastName());
                userProfile.setPreferredName(p.getPreferredName());
                userProfile.setUsername(p.getUsername());
                userProfile.setGoal(p.getGoal());
                userProfile.setProfilePhoto(p.getProfilePhoto());
                userProfile.setUserRole(p.getRole());
                userProfiles.add(userProfile);
            });
        }
        ;
        return userProfiles;
    }

    //TODO method to create one's own profile
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_EMPLOYEE"})
    public UserProfile addUserProfile(HttpServletRequest httpServletRequest, UserProfileRequest request) {

        MyUser user = extractUserFromToken.extractUserFromToken(httpServletRequest).getBody();

        assert user != null;
        return createUserProfileFromRequest(request, user);
    }

    //TODO method to update one's own profile
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_EMPLOYEE"})
    public UserProfile updateUserProfile(HttpServletRequest httpServletRequest, UserProfileRequest request) {

        MyUser user = extractUserFromToken.extractUserFromToken(httpServletRequest).getBody();
        assert user != null;
        return updateUserProfileFromRequest(request, user);
    }

    //TODO method to create another user's profile
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    public UserProfile addUserProfile(UserProfileRequest request, Long userId) {

        MyUser user = userRepository.findById(userId).orElseThrow();
        ;

        return createUserProfileFromRequest(request, user);
    }

    private UserProfile createUserProfileFromRequest(UserProfileRequest request, MyUser user) {
        UserProfile profile = new UserProfile();
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPreferredName(request.getPreferredName());
        profile.setUsername(request.getUsername());
        profile.setGoal(request.getGoal());
        assert user != null;
        profile.setUserId(user.getId());
        profile.setProfilePhoto(request.getProfilePhoto());

        return createUserProfile(profile);
    }


    //TODO method to update another user's profile
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    public UserProfile updateUserProfile(UserProfileRequest request, Long userId) {

        MyUser user = userRepository.findById(userId).orElseThrow();
        return updateUserProfileFromRequest(request, user);
    }

    private UserProfile updateUserProfileFromRequest(UserProfileRequest request, MyUser user) {
        UserProfile profile = new UserProfile();
        if (request.getFirstName() != null) {
            profile.setFirstName(request.getFirstName());
        } else {
            profile.setFirstName(userProfileRepository.findUserProfileByUserId(user.getId()).orElseThrow().getFirstName());
        }
        if (request.getLastName() != null) {
            profile.setLastName(request.getLastName());
        } else {
            profile.setLastName(userProfileRepository.findUserProfileByUserId(user.getId()).orElseThrow().getLastName());
        }
        if (request.getPreferredName() != null) {
            profile.setPreferredName(request.getPreferredName());
        } else {
            profile.setPreferredName(userProfileRepository.findUserProfileByUserId(user.getId()).orElseThrow().getPreferredName());
        }
        if (request.getUsername() != null) {
            profile.setUsername(request.getUsername());
        } else {
            profile.setUsername(userProfileRepository.findUserProfileByUserId(user.getId()).orElseThrow().getUsername());
        }
        if (request.getGoal() != null) {
            profile.setGoal(request.getGoal());
        } else {
            profile.setGoal(userProfileRepository.findUserProfileByUserId(user.getId()).orElseThrow().getGoal());
        }
        if (request.getProfilePhoto() != null) {
            profile.setProfilePhoto(request.getProfilePhoto());
        } else {
            profile.setProfilePhoto(userProfileRepository.findUserProfileByUserId(user.getId()).orElseThrow().getProfilePhoto());
        }

        profile.setUserId(user.getId());

        userProfileRepository.updateUserProfileByUserId(
                profile.getUserId(),
                profile.getFirstName(),
                profile.getGoal(),
                profile.getLastName(),
                profile.getPreferredName(),
                profile.getUsername(),
                profile.getProfilePhoto(),
                profile.getRole()
        );
        return userProfileRepository.findUserProfileByUserId(user.getId()).orElseThrow();
    }

    public UserProfile updateUserProfile(UserProfile updatedProfile) {
        return userProfileRepository.save(updatedProfile);
    }

    public UserProfile patchUserProfile(String username, JsonPatch patch) throws JsonPatchException, JsonProcessingException {
        UserProfile profile = fetchUserProfile(username);
        UserProfile patchedProfile = applyPatchToProfile(patch, profile);
        return updateUserProfile(patchedProfile);
    }

    private UserProfile applyPatchToProfile(JsonPatch patch, UserProfile targetProfile) throws JsonPatchException, JsonProcessingException {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(UserProfile.class, new UserProfileDeserializer());
        objectMapper.registerModule(simpleModule);
        try {
            JsonNode patched = patch.apply(objectMapper.convertValue(targetProfile, JsonNode.class));
            return objectMapper.treeToValue(patched, UserProfile.class);
        } catch (JsonPatchException e) {
            throw new JsonPatchException(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new JsonProcessingException(e.getMessage()) {
            };
        }
    }
}
