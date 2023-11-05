package com.likelion.remini.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generate(String subject, Date expiredAt) {
        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(expiredAt)
                .signWith(key) // HS512였음
                .compact();
    }

    public Authentication getAuthentication(String accessToken) throws ExpiredJwtException {
        Claims claims = parseClaims(accessToken);

        Collection<? extends GrantedAuthority> roles = new ArrayList<>();
        UserDetails user = new User(claims.getSubject(), "", roles);
        return new UsernamePasswordAuthenticationToken(user, "", roles);
    }

    public String extractSubject(String accessToken) {
        Claims claims = parseClaims(accessToken);
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = parseClaims(token);

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new RuntimeException("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT 토큰이 잘못되었습니다.");
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
