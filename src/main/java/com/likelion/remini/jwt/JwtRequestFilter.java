package com.likelion.remini.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String BEARER_TYPE = "Bearer";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // JWT 토큰을 추출
        String token = extractToken(request);

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰이 유효하면 사용자 인증 정보를 설정
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (RuntimeException e) {
            request.setAttribute("exception", e.getMessage());
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(StringUtils.hasText(token) && token.startsWith(BEARER_TYPE)) {
            return token.substring(7);    //"Bearer "를 뺀 값, 즉 토큰 값
        }

        return null;
    }
}

