package io.github.tiagofdev.applicationgla.config;

import io.github.tiagofdev.applicationgla.controller.JwtAuthenticationToken;
import io.github.tiagofdev.applicationgla.controller.JwtTokenManager;
import io.github.tiagofdev.applicationgla.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 *
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     *
     */
    private final JwtTokenManager tokenManager;
    /**
     *
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     *
     * @param tokenManager ,
     * @param userDetailsService ,
     */
    public JwtAuthenticationFilter(JwtTokenManager tokenManager, CustomUserDetailsService userDetailsService) {
        this.tokenManager = tokenManager;
        this.userDetailsService = userDetailsService;
    }

    /**
     *
     * @param request ,
     * @param response ,
     * @param chain ,
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");



        if (request.getRequestURI().equals("/register") || request.getRequestURI().equals("/auth/login") ||
                request.getRequestURI().equals("/actuator/**")) {
            chain.doFilter(request, response);  // Skip this filter for these endpoints
            return;
        }


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final String username = tokenManager.extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (tokenManager.isTokenValid(token, userDetails.getUsername())) {
                var authentication = new JwtAuthenticationToken(userDetails);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }
}
