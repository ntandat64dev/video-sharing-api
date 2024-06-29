package com.example.videosharingapi.config.security;

import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.util.JwtUtil;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final HandlerExceptionResolver resolver;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.jwtUtil = jwtUtil;
        this.resolver = resolver;
    }

    @Override
    protected void doFilterInternal(
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            final var authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                // If the request does not contain a Bearer Authentication header.
                filterChain.doFilter(request, response);
                return;
            }

            // Extract token from Authentication header.
            final String token = authHeader.substring(7);

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                // If the user is authenticated (by previous filters).
                filterChain.doFilter(request, response);
                return;
            }

            // Authenticate the token and set the SecurityContextHolder directly.
            // (we can use AuthenticationManager instead)
            var userDetails = jwtUtil.verifyToken(token);
            var authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (Exception e) {
            resolver.resolveException(request, response, null, new AppException(ErrorCode.AUTHENTICATION_ERROR));
            return;
        }

        filterChain.doFilter(request, response);
    }
}
