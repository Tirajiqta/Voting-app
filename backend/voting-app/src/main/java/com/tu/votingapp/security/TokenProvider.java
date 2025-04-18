package com.tu.votingapp.security;

import com.tu.votingapp.entities.RoleEntity;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface TokenProvider {
    String createToken(Long userId, Collection<RoleEntity> roles);
}
