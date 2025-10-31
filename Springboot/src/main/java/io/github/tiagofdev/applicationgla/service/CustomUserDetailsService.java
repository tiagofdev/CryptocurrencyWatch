package io.github.tiagofdev.applicationgla.service;

import io.github.tiagofdev.applicationgla.model.UserEntity;
import io.github.tiagofdev.applicationgla.repository.UserEntityRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(UserEntityRepository userEntityRepository, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("UserEntity not found"));

        return User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .build();
    }


    public void createUser(String username, String plainPassword) {
        if (userEntityRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists!");
        }

        // Encrypt the password
        String encodedPassword = passwordEncoder.encode(plainPassword);

        // Save the new user
        UserEntity user = new UserEntity(username, encodedPassword);
        userEntityRepository.save(user);
    }

}
