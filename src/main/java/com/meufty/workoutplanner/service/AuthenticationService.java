package com.meufty.workoutplanner.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meufty.workoutplanner.model.AuthenticationRequest;
import com.meufty.workoutplanner.model.AuthenticationResponse;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.repository.UserRepository;
import com.meufty.workoutplanner.util.JwtUtil;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${spring.security.secret.jwt.secret.createLoginTokenExpirationInMs}")
    private static long LOGIN_EXPIRY_TIME_MS;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private JwtUtil jwtTokenUtil;
    @Autowired
    private UserRepository userRepository;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e){
            throw new BadCredentialsException("Incorrect Username or Password", e);
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails, LOGIN_EXPIRY_TIME_MS);
        final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails.getUsername());
        MyUser user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return new AuthenticationResponse(jwt, refreshToken, user.getUserRole());
    }
    public ResponseEntity<?> refreshToken(HttpServletRequest request) throws Exception {
       //Get the claims from the request
        DefaultClaims claims = (DefaultClaims) request.getAttribute("claims");
        HashMap<String, Object> expectedMap = new HashMap<>(claims);
        String token = jwtTokenUtil.generateRefreshToken(expectedMap, expectedMap.get("sub").toString());
        MyUser myUser = userRepository.findByUsername(jwtTokenUtil.extractUsername(token)).orElseThrow();
        return ResponseEntity.ok(new AuthenticationResponse(token, request.getAttribute("refreshToken").toString(), myUser.getUserRole()));
    }
}
