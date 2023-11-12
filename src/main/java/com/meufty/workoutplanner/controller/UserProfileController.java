package com.meufty.workoutplanner.controller;

import com.meufty.workoutplanner.api.UserProfileRequest;
import com.meufty.workoutplanner.model.UserRole;
import com.meufty.workoutplanner.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping(path = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Secured({"ROLE_ADMIN", "ROLE_EMPLOYEE", "ROLE_USER"})
    public ResponseEntity<?> createUserProfile(@RequestParam(value = "profilePhoto") MultipartFile file, @RequestParam(value = "firstName") String firstName, @RequestParam(value = "lastName") String lastName, @RequestParam(value = "preferredName") String preferredName, @RequestParam(value = "goal") String goal, @RequestParam(value = "userRole") UserRole role, HttpServletRequest httpServletRequest) throws IOException {
        UserProfileRequest profileRequest = new UserProfileRequest();
        profileRequest.setFirstName(firstName);
        profileRequest.setLastName(lastName);
        profileRequest.setPreferredName(preferredName);
        profileRequest.setGoal(goal);
        profileRequest.setUserRole(role);
        profileRequest.setProfilePhoto(file.getBytes());
        return ResponseEntity.ok(userProfileService.adduserProfile(httpServletRequest, profileRequest));
    }
}
