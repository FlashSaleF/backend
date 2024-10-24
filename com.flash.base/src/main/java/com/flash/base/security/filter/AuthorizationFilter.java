package com.flash.base.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j(topic = "Authorization Filter")
public class AuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (requestURI.equals("/actuator/prometheus")) {
            filterChain.doFilter(request, response);
            return;
        }

        String userId = request.getHeader("X-User-ID");
        String role = request.getHeader("X-User-Role");
        log.info("role: {}", role);
        if (userId != null && role != null) {
            User user = new User(userId, "", Collections.singletonList(new SimpleGrantedAuthority(role)));
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
