package com.meufty.workoutplanner.service;

import com.meufty.workoutplanner.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    @Autowired
    private TokenRepository tokenRepository;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authorizationHeader = request.getHeader("Authorization");
        Cookie[] cookies = request.getCookies();

        String jwt = null;
        String refreshToken = null;
        if (cookies.length > 0){
            refreshToken = extractRefreshTokenFromCookie(cookies[0].toString());
        }

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        }

        var storedToken = tokenRepository.findByToken(jwt).orElse(null);
        if (storedToken != null){
            storedToken.setRevoked(true);
            storedToken.setExpired(true);
            tokenRepository.delete(storedToken);
        }

        var storeRefreshToken = tokenRepository.findByToken(refreshToken).orElse(null);
        if (storeRefreshToken != null){
            storeRefreshToken.setRevoked(true);
            storeRefreshToken.setExpired(true);
            tokenRepository.delete(storeRefreshToken);
        }
    }
    
    public String extractRefreshTokenFromCookie(@CookieValue(name = "refreshToken") String refreshToken){
        return refreshToken;
    }
}
