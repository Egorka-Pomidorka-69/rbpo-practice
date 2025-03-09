package ru.mtuci.demo.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(httpServletRequest);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String tokenType = jwtTokenProvider.getTokenType(token);
            if ("access".equals(tokenType))
                SecurityContextHolder.getContext().setAuthentication(jwtTokenProvider.getAuthentication(token));
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }

    private String resolveToken(HttpServletRequest httpServletRequest) {

        String bearerToken = httpServletRequest.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) return bearerToken.substring(7);

        return null;

    }

}