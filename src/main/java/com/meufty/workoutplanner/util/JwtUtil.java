package com.meufty.workoutplanner.util;

import com.meufty.workoutplanner.model.MyUser;
import com.meufty.workoutplanner.model.MyUserDetails;
import com.meufty.workoutplanner.model.Token;
import com.meufty.workoutplanner.repository.TokenRepository;
import com.meufty.workoutplanner.repository.UserRepository;
import com.meufty.workoutplanner.service.LogoutService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtUtil {

    @Value ("${spring.security.secret.jwt.secret.key}")
    private String SECRET_KEY;

    @Value("${spring.security.secret.jwt.secret.generateTokenExpirationInMs}")
    private long GENERATE_TOKEN_EXPIRY_MS;

    @Value("${spring.security.secret.jwt.secret.refreshTokenExpirationInMs}")
    private long REFRESH_TOKEN_EXPIRY_MS;

    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenRepository tokenRepository;

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), GENERATE_TOKEN_EXPIRY_MS);
    }

    public String generateToken(UserDetails userDetails, long expiryTimeInMs){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername(), expiryTimeInMs);
    }

    private String createToken(Map<String, Object> claims, String subject, long expiryTimeInMs){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiryTimeInMs))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String generateRefreshToken(UserDetails myUserDetails){
        return Jwts.builder().setClaims(new HashMap<>())
                .setSubject(myUserDetails.getUsername()).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY_MS))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public String generateRefreshToken(HashMap<String, Object> map, String subject){
        return Jwts.builder().setClaims(map).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY_MS))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public void deleteAllUserTokens(String refreshToken) {
        var username = extractUsername(refreshToken);
        MyUser myUser = userRepository.findByUsername(username).orElse(null);
        assert myUser != null;
        List<Token> tokens = tokenRepository.findAllByUser(myUser);
        if (tokens.isEmpty()) return;
        //Revoke, expire and delete all jwt tokens the current belong to the user
        tokenRepository.deleteAll(tokens);
    }

    public void deleteAllUserTokens(MyUser myUser) {
        List<Token> tokens = tokenRepository.findAllByUser(myUser);
        //Revoke, expire and delete all jwt tokens the current belong to the user
        if (tokens.isEmpty()) return;
        tokenRepository.deleteAll(tokens);
    }

    public void revokeUserJwtTokens(MyUser myUser){
        var validUserAccessTokens = tokenRepository.findAllJwtTokensByUser(myUser.getId());
        if (validUserAccessTokens.isEmpty()) return;
        tokenRepository.deleteAll(validUserAccessTokens);
    }

    //TODO: add methods to revoke both tokens, as well as other parameters of token such as isValid, is Revoked, etc
}
