package com.example.videosharingapi.config.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class AuthenticatedUser implements UserDetails {

    @Getter
    private final String userId;

    private final String username;

    private final String password;

    private final List<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public static AuthenticatedUserBuilder builder() {
        return new AuthenticatedUserBuilder();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AuthenticatedUserBuilder {
        private String userId;
        private String username;
        private String password;
        private List<GrantedAuthority> authorities;

        public AuthenticatedUserBuilder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public AuthenticatedUserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public AuthenticatedUserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public AuthenticatedUserBuilder roles(List<String> roles) {
            this.authorities = new ArrayList<>(roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList());
            return this;
        }

        public AuthenticatedUserBuilder scopes(List<String> scopes) {
            this.authorities = new ArrayList<>(scopes.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList());
            return this;
        }

        public AuthenticatedUser build() {
            return new AuthenticatedUser(this.userId, this.username, this.password, this.authorities);
        }
    }
}
