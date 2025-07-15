package br.com.convivium.service;

import br.com.convivium.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        br.com.convivium.entity.User userEntity = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with CPF: " + cpf));

        return new User(
                userEntity.getCpf(), // o CPF será o "username" usado internamente pelo Spring
                userEntity.getPassword(),
                List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(userEntity.getRole().getName()))
        );
    }
}


