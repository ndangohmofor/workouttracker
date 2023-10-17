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
    public String confirm(@RequestParam("token") String token){
        return registrationService.confirmToken(token);
    }
}
