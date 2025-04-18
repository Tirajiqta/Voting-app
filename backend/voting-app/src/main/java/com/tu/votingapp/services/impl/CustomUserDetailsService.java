package com.tu.votingapp.services.impl;

import com.tu.votingapp.entities.UserEntity;
import com.tu.votingapp.repositories.interfaces.UserRepository;
import com.tu.votingapp.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String egn) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEgn(egn)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with EGN: " + egn));
        return UserPrincipal.fromEntity(user);
    }
}
