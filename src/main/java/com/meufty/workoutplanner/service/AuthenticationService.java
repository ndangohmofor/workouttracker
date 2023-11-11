package com.meufty.workoutplanner.service;

import com.meufty.workoutplanner.api.AuthenticationRequest;
import com.meufty.workoutplanner.api.AuthenticationResponse;
import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.model.Token;
import com.meufty.workoutplanner.model.TokenType;
import com.meufty.workoutplanner.repository.TokenRepository;
import com.meufty.workoutplanner.repository.UserRepository;
import com.meufty.workoutplanner.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Value("${spring.security.secret.jwt.secret.createLoginTokenExpirationInMs}")
    private long LOGIN_EXPIRY_TIME_MS;
    AuthenticationManager authenticationManager;
    MyUserDetailsService myUserDetailsService;
    JwtUtil jwtTokenUtil;
    UserRepository userRepository;
    TokenRepository tokenRepository;
    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest authenticationRequest, HttpServletResponse servletResponse) {
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
        var refreshjwt = saveUserGeneratedToken(refreshToken, user, TokenType.REFRESH);
        var token = saveUserGeneratedToken(jwt, user, TokenType.BEARER);
        jwtTokenUtil.deleteAllUserTokens(user);
        tokenRepository.save(refreshjwt);
        tokenRepository.save(token);
        AuthenticationResponse response = new AuthenticationResponse(jwt, refreshToken, user.getUsername(), user.getUserRole());
        ResponseCookie authCookie = getRefreshTokenCookie(refreshjwt);
        servletResponse.addHeader("Set-Cookie", authCookie.toString());
        return ResponseEntity.ok().body(response);
    }

    private static Token saveUserGeneratedToken(String token, MyUser user, TokenType type) {
        return Token.builder().user(user).token(token).tokenType(type).expired(false).revoked(false).build();
    }

    public ResponseEntity<AuthenticationResponse> refreshToken(String refreshToken) throws Exception {
        String username = jwtTokenUtil.extractUsername(refreshToken);
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
        HashMap<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("role", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        String token = jwtTokenUtil.generateRefreshToken(expectedMap, username);
        MyUser myUser = userRepository.findByUsername(jwtTokenUtil.extractUsername(token)).orElseThrow();
        jwtTokenUtil.revokeUserJwtTokens(myUser);
        Token newToken = saveUserGeneratedToken(token, myUser, TokenType.BEARER);
        tokenRepository.save(newToken);
        AuthenticationResponse response = new AuthenticationResponse(token, refreshToken, myUser.getUsername(), myUser.getUserRole());
        return ResponseEntity.ok(response);
    }

    private static ResponseCookie getRefreshTokenCookie(Token newToken) {
        return ResponseCookie.from("refreshToken", newToken.getToken()).httpOnly(true)
                //TODO: Enable the secure parameter to get the cookie to be transmitted over https
//                .secure(true)
                .maxAge(86400).domain("localhost").build();
    }
}
