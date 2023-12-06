package com.meufty.workoutplanner.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.meufty.workoutplanner.api.UserProfileRequest;
import com.meufty.workoutplanner.model.UserProfile;
import com.meufty.workoutplanner.model.UserRole;
import com.meufty.workoutplanner.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/profiles")
public class UserProfileController {

    @Autowired
    UserProfileService userProfileService;

    @GetMapping(path = "/profile")
    @Secured({"ROLE_ADMIN", "ROLE_USER", "ROLE_EMPLOYEE"})
    public ResponseEntity<?> fetchUserProfile(HttpServletRequest request) {
        try {
            return ResponseEntity.ok(userProfileService.fetchUserProfile(request));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(410).body(ex.getMessage());
        }
    }

    @GetMapping(path = "/profile/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    private ResponseEntity<?> fetchUserProfile(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userProfileService.fetchUserProfile(id));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(410).body(ex.getMessage());
        }
    }

    @PostMapping(path = "/profile/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_USER"})
    public ResponseEntity<?> createUserProfile(@RequestParam(value = "profilePhoto") MultipartFile file, @RequestParam(value = "firstName") String firstName, @RequestParam(value = "lastName") String lastName, @RequestParam(value = "preferredName") String preferredName, @RequestParam(value = "goal") String goal, @RequestParam(value = "username") String username, @RequestParam(value = "userRole") UserRole role, HttpServletRequest httpServletRequest) throws IOException {
        UserProfileRequest profileRequest = new UserProfileRequest();
        profileRequest.setFirstName(firstName);
        profileRequest.setLastName(lastName);
        profileRequest.setPreferredName(preferredName);
        profileRequest.setGoal(goal);
        profileRequest.setUserRole(role);
        profileRequest.setUsername(username);
        profileRequest.setProfilePhoto(file.getBytes());
        return ResponseEntity.ok(userProfileService.addUserProfile(httpServletRequest, profileRequest));
    }

    @PostMapping(path = "/profile/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_USER"})
    public ResponseEntity<?> updateUserProfile(@RequestParam(value = "profilePhoto", required = false) MultipartFile file, @RequestParam(value = "firstName", required = false) String firstName, @RequestParam(value = "lastName", required = false) String lastName, @RequestParam(value = "preferredName", required = false) String preferredName, @RequestParam(value = "username", required = false) String username, @RequestParam(value = "goal", required = false) String goal, @RequestParam(value = "userRole", required = false) UserRole role, HttpServletRequest httpServletRequest) throws IOException {
        UserProfileRequest profileRequest = new UserProfileRequest();
        if (firstName != null) profileRequest.setFirstName(firstName);
        if (lastName != null) profileRequest.setLastName(lastName);
        if (preferredName != null) profileRequest.setPreferredName(preferredName);
        if (goal != null) profileRequest.setGoal(goal);
        if (role != null) profileRequest.setUserRole(role);
        if (username != null) profileRequest.setUsername(username);
        if (file != null) profileRequest.setProfilePhoto(file.getBytes());
        return ResponseEntity.ok(userProfileService.updateUserProfile(httpServletRequest, profileRequest));
    }

    @PatchMapping(path = "/profile/{username}/update", consumes = "application/json-patch+json")
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_USER"})
    public ResponseEntity<?> updateUserProfile(@PathVariable String username, @RequestBody JsonPatch patch) {
        try {
            UserProfile profile = userProfileService.fetchUserProfile(username);
            UserProfile patchedProfile = applyPatchToProfile(patch, profile);
            userProfileService.updateUserProfile(patchedProfile);
            return ResponseEntity.ok(patchedProfile);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(410).body(ex.getMessage());
        }
    }

    private UserProfile applyPatchToProfile(JsonPatch patch, UserProfile targetProfile) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(targetProfile, JsonNode.class));
        return objectMapper.treeToValue(patched, UserProfile.class);

    }

}
