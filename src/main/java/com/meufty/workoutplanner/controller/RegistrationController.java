package com.meufty.workoutplanner.controller;

import com.meufty.workoutplanner.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.meufty.workoutplanner.api.RegistrationRequest;

@CrossOrigin
@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    private RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<String> register(@RequestBody RegistrationRequest request){
        try {
            return ResponseEntity.ok(registrationService.register(request));
        } catch (IllegalStateException e){
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @GetMapping(path = "confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token){
        //TODO: As a future iteration, we can update the token confirmation such that when a user confirms their token, if this is successful, the username and password are extracted from the token and used to authenticate and login the user immediated
        //TODO: This means this method will need to return again an access token and a refresh token
        return ResponseEntity.ok(registrationService.confirmToken(token));
    }
}
