package com.meufty.workoutplanner.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meufty.workoutplanner.model.AuthenticationRequest;
import com.meufty.workoutplanner.model.AuthenticationResponse;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.repository.UserRepository;
import com.meufty.workoutplanner.util.JwtUtil;
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

    @Autowired
    private JwtUtil jwtUtil;
    private final UserRepository repository;

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
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        MyUser myUser = null;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")){
            return;
        }
        refreshToken = authHeader.substring(7);
        String username = jwtUtil.extractUsername(refreshToken);
        if (username != null){
            myUser = repository.findByUsername(username)
                    .orElseThrow();
        }

        assert myUser != null;
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(myUser.getUsername());

        if (jwtUtil.validateToken(refreshToken, userDetails)){
            var accessToken = jwtUtil.generateToken(userDetails);
            var authResponse = new AuthenticationResponse(accessToken, refreshToken, myUser.getUserRole());
            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
        }
    }
}
