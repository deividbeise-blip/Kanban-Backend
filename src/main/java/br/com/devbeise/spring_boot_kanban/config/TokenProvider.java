package br.com.devbeise.spring_boot_kanban.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenProvider {

    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    @Value("${jwt.expiration}")
    private Long expirationTime;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpirationTime;

    @Value("${jwt.secret}")
    private String key;

    public String gerarToken(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return buildToken(user.getUsername(), TYPE_ACCESS, expirationTime);
    }

    public String gerarToken(String username) {
        return buildToken(username, TYPE_ACCESS, expirationTime);
    }

    public String gerarRefreshToken(String username) {
        return buildToken(username, TYPE_REFRESH, refreshExpirationTime);
    }

    private String buildToken(String username, String type, long validityMs) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validityMs);

        return Jwts.builder()
                .subject(username)
                .claim(CLAIM_TYPE, type)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(key.getBytes());
    }

    public boolean isAccessTokenValid(String token) {
        return isValidOfType(token, TYPE_ACCESS);
    }

    public boolean isRefreshTokenValid(String token) {
        return isValidOfType(token, TYPE_REFRESH);
    }

    private boolean isValidOfType(String token, String expectedType) {
        try {
            return expectedType.equals(getClaims(token).get(CLAIM_TYPE, String.class));
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }
}