package com.meufty.workoutplanner.controller;

import com.meufty.workoutplanner.model.AuthenticationRequest;
import com.meufty.workoutplanner.model.AuthenticationResponse;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.model.MyUserDetails;
import com.meufty.workoutplanner.repository.UserRepository;
import com.meufty.workoutplanner.service.MyUserDetailsService;
import com.meufty.workoutplanner.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/v1")
public class AuthenticationController {

    private AuthenticationManager authenticationManager;
    private MyUserDetailsService myUserDetailsService;
    private JwtUtil jwtTokenUtil;
    private UserRepository userRepository;

    @PostMapping(path = "/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws BadCredentialsException {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e){
            throw new BadCredentialsException("Incorrect Username or Password", e);
        }
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        MyUser user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(new AuthenticationResponse(jwt, user.getUserRole()));
    }
}
