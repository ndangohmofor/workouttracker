package com.meufty.workoutplanner.controller;

import com.meufty.workoutplanner.service.RegistrationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.meufty.workoutplanner.api.RegistrationRequest;

@CrossOrigin
@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    private RegistrationService registrationService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest request){
        try {
            return registrationService.register(request);
        } catch (IllegalStateException e){
            return e.getMessage();
        }
    }

    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token){
        return registrationService.confirmToken(token);
    }
}
