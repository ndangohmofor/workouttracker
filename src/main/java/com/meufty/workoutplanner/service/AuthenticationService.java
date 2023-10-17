package com.meufty.workoutplanner.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meufty.workoutplanner.model.AuthenticationResponse;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.model.MyUserDetails;
import com.meufty.workoutplanner.repository.UserRepository;
import com.meufty.workoutplanner.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Autowired
    private JwtUtil jwtUtil;
    private final UserRepository respository;
    private final MyUserDetailsService myUserDetailsService;
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String email;
        MyUser myUser = null;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")){
            return;
        }
        refreshToken = authHeader.substring(7);
        String username = jwtUtil.extractUsername(refreshToken);
        if (username != null){
            myUser = respository.findByUsername(username)
                    .orElseThrow();
        }

        UserDetails userDetails = myUserDetailsService.loadUserByUsername(myUser.getUsername());

        if (jwtUtil.validateToken(refreshToken, userDetails)){
            var accessToken = jwtUtil.generateToken(userDetails);
            var authResponse = new AuthenticationResponse(accessToken, refreshToken, myUser.getUserRole());
            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
        }
    }
}
