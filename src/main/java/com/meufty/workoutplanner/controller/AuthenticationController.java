package com.meufty.workoutplanner.controller;

import com.meufty.workoutplanner.api.AuthenticationRequest;
import com.meufty.workoutplanner.service.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createAuthenticationToken(@RequestBody @Valid AuthenticationRequest authenticationRequest, HttpServletResponse response) throws BadCredentialsException {
       return ResponseEntity.status(200).body(service.authenticate(authenticationRequest, response));
    }

    @GetMapping(path = "/refreshtoken")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken") String refreshToken) throws Exception {
        return ResponseEntity.ok(service.refreshToken(refreshToken));
    }
}
