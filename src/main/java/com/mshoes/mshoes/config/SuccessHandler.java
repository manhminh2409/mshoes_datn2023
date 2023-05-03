package com.mshoes.mshoes.config;

import io.jsonwebtoken.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public class SuccessHandler implements AuthenticationSuccessHandler {
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, java.io.IOException {
        boolean hasRoleUser = false;
        boolean hasAdmin = false;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            if (grantedAuthority.getAuthority().equals("ROLE_USER")) {
                hasRoleUser = true;
                break;
            } else if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
                hasAdmin = true;
                break;
            }
        }
        //
        if (hasRoleUser) {
            redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/home");
        } else if (hasAdmin) {
            redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, "/admin/home");
        } else {
            throw new IllegalStateException();
        }
    }

}
