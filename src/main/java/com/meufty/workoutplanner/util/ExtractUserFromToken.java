package com.meufty.workoutplanner.util;

import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Component
@AllArgsConstructor
public class ExtractUserFromToken {

    JwtUtil jwtUtil;
    UserRepository userRepository;
    public ResponseEntity<MyUser> extractUserFromToken(HttpServletRequest request){
        String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }
        return ResponseEntity.ok(userRepository.findByUsername(username).orElseThrow());
    }
}
