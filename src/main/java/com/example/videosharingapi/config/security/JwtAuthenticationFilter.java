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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final HandlerExceptionResolver resolver;

    public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil,
                                   @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.userDetailsService = userDetailsService;
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

            // Get username from token.
            var username = jwtUtil.extractUsername(token);

            if (username == null && SecurityContextHolder.getContext().getAuthentication() != null) {
                // If there is no username in token or user is authenticated (by previous filters).
                filterChain.doFilter(request, response);
                return;
            }

            // Authenticate token (we can also use a custom AuthenticationProvider for this).
            jwtUtil.verifyToken(token);
            // If there is no exception then token is valid.
            // Load UserDetails and set to SecurityContext.
            var userDetails = userDetailsService.loadUserByUsername(username);
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
