package com.meufty.workoutplanner.controller;

import com.meufty.workoutplanner.model.AuthenticationRequest;
import com.meufty.workoutplanner.model.AuthenticationResponse;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.repository.UserRepository;
import com.meufty.workoutplanner.service.AuthenticationService;
import com.meufty.workoutplanner.service.MyUserDetailsService;
import com.meufty.workoutplanner.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1")
public class AuthenticationController {

    @Autowired
    AuthenticationService service;

    @PostMapping(path = "/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody @Valid AuthenticationRequest authenticationRequest) throws BadCredentialsException {
       return ResponseEntity.status(200).body(service.authenticate(authenticationRequest));
    }

    @PostMapping(path = "/refreshtoken")
    public void refreshToken(@RequestBody @Valid HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.refreshToken(request, response);
    }
}
