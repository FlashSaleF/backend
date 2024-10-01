package com.flash.order;

import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.Collections;

@Slf4j
public class SecurityContextUserFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain
    ) throws IOException, ServletException {

        log.info("SecurityContextUserFilter Pass : Setting test userId '1' , role 'CUSTOMER' ");

        User user = new User(
                "1",
                "",
                Collections.singletonList(new SimpleGrantedAuthority("CUSTOMER"))
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("Authentication in SecurityContext: {}", SecurityContextHolder.getContext().getAuthentication());

        chain.doFilter(request, response);
    }
}
