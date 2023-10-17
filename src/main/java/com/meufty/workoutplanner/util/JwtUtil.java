package com.meufty.workoutplanner.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Value ("${spring.security.secret.jwt.secret.key}")
    private static String SECRET_KEY;

    @Value("${spring.security.secret.jwt.secret.generateTokenExpirationInMs}")
    private static long GENERATE_TOKEN_EXPIRY_MS;

    @Value("${spring.security.secret.jwt.secret.refreshTokenExpirationInMs}")
    private long REFRESH_TOKEN_EXPIRY_MS;

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

    private Claims extractAllClaims(String token){
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
        return createToken(claims, userDetails.getUsername(), expiryTimeInMs);
    }

    private String createToken(Map<String, Object> claims, String subject, long expiryTimeInMs){
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() +
                expiryTimeInMs)).signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public String generateRefreshToken(String subject){
        return Jwts.builder().setClaims(new HashMap<>()).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY_MS))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    //TODO: add methods to revoke both tokens, as well as other parameters of token such as isValid, is Revoked, etc
}
