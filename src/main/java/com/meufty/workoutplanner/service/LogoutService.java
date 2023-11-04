package com.meufty.workoutplanner.service;

import com.meufty.workoutplanner.repository.TokenRepository;
import com.meufty.workoutplanner.repository.UserRepository;
import com.meufty.workoutplanner.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LogoutService implements LogoutHandler {

    public TokenRepository tokenRepository;
    public JwtUtil jwtUtil;
    public UserRepository userRepository;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Cookie refreshTokenCookie = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("refreshToken")).collect(Collectors.toList()).get(0);

        var refreshToken = refreshTokenCookie.getValue();

        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setValue(null);

        jwtUtil.deleteAllUserTokens(refreshToken);

        ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()).build();
    }

}
