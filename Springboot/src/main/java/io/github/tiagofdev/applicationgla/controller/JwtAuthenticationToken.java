package io.github.tiagofdev.applicationgla.controller;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    /**
     *
     */
    private final UserDetails principal;

    /**
     *
     * @param principal
     */
    public JwtAuthenticationToken(UserDetails principal) {
        super(principal.getAuthorities());
        this.principal = principal;
        setAuthenticated(true);
    }

    /**
     *
     * @return Object
     */
    @Override
    public Object getCredentials() {
        return null; // No credentials needed for JWT
    }

    /**
     *
     * @return Object
     */
    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
