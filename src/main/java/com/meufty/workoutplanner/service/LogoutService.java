package com.meufty.workoutplanner.service;

import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.model.MyUserDetails;
import com.meufty.workoutplanner.model.Token;
import com.meufty.workoutplanner.repository.TokenRepository;
import com.meufty.workoutplanner.repository.UserRepository;
import com.meufty.workoutplanner.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        List<Cookie> cookies = Arrays.stream(request.getCookies()).filter(cookie -> cookie.getName().equals("refreshToken")).collect(Collectors.toList());

        String refreshToken = null;
        if (!cookies.isEmpty()){
            refreshToken = cookies.get(0).getValue();
        }

        var storeRefreshToken = tokenRepository.findByToken(refreshToken).orElse(null);
        if (storeRefreshToken != null){
            storeRefreshToken.setRevoked(true);
            storeRefreshToken.setExpired(true);
            tokenRepository.delete(storeRefreshToken);
        }

        var username = jwtUtil.extractUsername(refreshToken);
        MyUser myUser = userRepository.findByUsername(username).orElse(null);
        assert myUser != null;
        List<Token> tokens = tokenRepository.findAllByUser(myUser);

        //Revoke, expire and delete all jwt tokens the current belong to the user
        tokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
            tokenRepository.delete(token);
        });
    }
}
