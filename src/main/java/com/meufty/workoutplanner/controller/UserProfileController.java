package com.meufty.workoutplanner.controller;

import com.meufty.workoutplanner.api.UserProfileRequest;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.service.UserProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
@CrossOrigin
@Slf4j
@RequestMapping(path = "/api/v1/profile")
public class UserProfileController {
    private UserProfileService userProfileService;

    @GetMapping
    private ResponseEntity<?> fetchUserProfile(HttpServletRequest request) {
        try {
            return ResponseEntity.ok(userProfileService.fetchUserProfile(request));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(410).body(ex.getMessage());
        }
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN, ROLE_EMPLOYEE')")
    private ResponseEntity<?> fetchUserProfile(@RequestParam("id") Long userId) {
        try {
            return ResponseEntity.ok(userProfileService.fetchUserProfile(userId));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(410).body(ex.getMessage());
        }
    }
}
