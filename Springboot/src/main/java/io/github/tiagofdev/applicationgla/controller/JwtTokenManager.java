package io.github.tiagofdev.applicationgla.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.security.Key;
import java.util.function.Function;

/**
 *
 */
@Service
public class JwtTokenManager {

    /**
     *
     */
    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Use HS256 for signing

    /**
     * Generate a JWT Token using Authentication object
     * @param username ,
     * @return String
     */
    public String generateToken(String username) {
        // 1 hour
        final long EXPIRATION_TIME = (long) 1000 * 60 * 15;
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }

    /**
     *
     * @param token ,
     * @return String
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     *
     * @param token ,
     * @param claimsResolver ,
     * @return T
     * @param <T> ,
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    /**
     *
     * @param token ,
     * @param username ,
     * @return boolean
     */
    public boolean isTokenValid(String token, String username) {
        String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     *
     * @param token ,
     * @return boolean
     */
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
