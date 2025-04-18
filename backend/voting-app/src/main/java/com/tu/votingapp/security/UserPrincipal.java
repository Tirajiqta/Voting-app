package com.tu.votingapp.security;

import com.tu.votingapp.entities.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserPrincipal implements UserDetails {
    private final Long id;

    /**
     * National identifier used for login.
     */
    private final String egn;

    /**
     * Document number used as credential for login.
     */
    private final String documentNumber;

    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id,
                         String egn,
                         String documentNumber,
                         Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.egn = egn;
        this.documentNumber = documentNumber;
        this.authorities = authorities;
    }

    /**
     * Build UserPrincipal from UserEntity, extracting EGN and DocumentEntity.number.
     */
    public static UserPrincipal fromEntity(UserEntity user) {
        List<GrantedAuthority> auths = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return new UserPrincipal(
                user.getId(),
                user.getEgn(),
                user.getDocument().getNumber(),
                auths
        );
    }

    @Override
    public String getUsername() {
        return egn;
    }

    @Override
    public String getPassword() {
        return documentNumber;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
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
}

