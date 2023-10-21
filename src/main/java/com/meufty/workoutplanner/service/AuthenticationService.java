package com.meufty.workoutplanner.service;

import com.meufty.workoutplanner.model.*;
import com.meufty.workoutplanner.repository.TokenRepository;
import com.meufty.workoutplanner.repository.UserRepository;
import com.meufty.workoutplanner.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${spring.security.secret.jwt.secret.createLoginTokenExpirationInMs}")
    private long LOGIN_EXPIRY_TIME_MS;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private JwtUtil jwtTokenUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect Username or Password", e);
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails, LOGIN_EXPIRY_TIME_MS);
        final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
        MyUser user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        var refreshjwt = saveUserGeneeratedToken(refreshToken, user);
        var token = saveUserGeneeratedToken(jwt, user);
        tokenRepository.save(refreshjwt);
        tokenRepository.save(token);
        return new AuthenticationResponse(jwt, refreshToken, user.getUserRole());
    }

    private static Token saveUserGeneeratedToken(String token, MyUser user) {
        return Token.builder()
                .user(user)
                .token(token)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
    }

    public ResponseEntity<?> refreshToken(HttpServletRequest request) throws Exception {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String refreshToken = authHeader.substring(7);
        String username = jwtTokenUtil.extractUsername(refreshToken);
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
        HashMap<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("role", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        String token = jwtTokenUtil.generateRefreshToken(expectedMap, username);
        MyUser myUser = userRepository.findByUsername(jwtTokenUtil.extractUsername(token)).orElseThrow();
        saveUserGeneeratedToken(token, myUser);
        return ResponseEntity.ok(new AuthenticationResponse(token, refreshToken, myUser.getUserRole()));
    }
}
