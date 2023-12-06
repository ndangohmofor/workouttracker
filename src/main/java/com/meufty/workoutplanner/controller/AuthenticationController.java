package com.meufty.workoutplanner.controller;

import com.meufty.workoutplanner.api.AuthenticationRequest;
import com.meufty.workoutplanner.api.AuthenticationResponse;
import com.meufty.workoutplanner.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1")
public class AuthenticationController {

    @Autowired
    AuthenticationService service;

    @PostMapping(path = "/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody @Valid AuthenticationRequest authenticationRequest, HttpServletResponse response) throws BadCredentialsException {
       return service.authenticate(authenticationRequest, response);
    }

    @GetMapping(path = "/refreshtoken")
    public AuthenticationResponse refreshToken(@CookieValue(name = "refreshToken") String refreshToken) throws Exception {
        return (service.refreshToken(refreshToken));
    }
}
